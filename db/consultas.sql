DROP VIEW IF EXISTS VideoLikes;

-- Visão que representa o id de vídeo e o número de likes de vídeos
CREATE VIEW VideoLikes AS
SELECT Video.id, COUNT(*) AS VideoLikes
FROM Video NATURAL LEFT JOIN Visualization
WHERE Visualization.liked = TRUE
GROUP BY Video.id;

-- Consulta que representa o maior número de likes em um vídeo por categoria que tem vídeos com likes
SELECT videocategory.name AS Category, max(videolikes) AS MaxLikes
FROM videocategory left join (videolikes natural join video) on (name = videocategoryname)
GROUP BY videocategory.name
HAVING max(videolikes) > 0;

-- Consulta que representa a quantidade total de likes em vídeos por canal
SELECT channel.displayname AS Channel, sum(videolikes) AS Likes
FROM (video natural join videolikes) join post on (video.postid = post.id) join channel on (post.authorusername = channel.username)
GROUP BY channel.username;

-- Consulta que representa vídeos que receberam comentário menos de 1 minuto após a publicação
SELECT video.title AS Video
FROM video join post as videopost on (video.postid = videopost.id) join
    (comment join post as commentpost on (comment.postid = commentpost.id)) on (video.postid = comment.attachedpostid)
WHERE commentpost.date < videopost.date + '1 minute';

-- Consulta que representa os canais que postaram nenhum vídeo há menos de uma semana
SELECT channel.displayname AS Channel
FROM channel join (video join post on (video.postid = post.id)) on (channel.username = post.authorusername)
EXCEPT
SELECT channel.displayname
FROM channel join (video join post on (video.postid = post.id)) on (channel.username = post.authorusername)
WHERE post.date > date_subtract(current_timestamp, '1 week');

-- Consulta que representa canais brasileiros com vídeos com legenda em português
SELECT displayname AS Channel
FROM channel
WHERE country = 'Brazil' and username in
    (SELECT post.authorusername
     FROM video join post on (video.postid = post.id)
     WHERE exists
         (SELECT language
         FROM videosubtitle
         WHERE language = 'pt'));

-- Consulta que representa vídeos com mais de 10 minutos e tag 'vlog' de canais verificados
SELECT video.title AS Video
FROM channel join (video join post on (video.postid = post.id)) on (channel.username = post.authorusername)
where video.length > 600 and channel.isverified = TRUE and exists
    (SELECT *
     FROM tag
     WHERE tag = 'vlog' and videoid = video.id);

-- Consulta que representa canais de contas premium e seus comentários
SELECT channel.displayname AS Channel, comment.text AS Comment
FROM account join channel on (account.email = channel.accountemail) right join
    (comment join post on (comment.postid = post.id)) on (channel.username = post.authorusername)
where account.ispremium = TRUE;

-- Consulta que representa canais brasileiros e o número de publicações e inscritos
SELECT channel.displayname AS Channel, count(distinct post.id) As Posts, count(distinct subscription.spectatorusername) As Subscribers
FROM channel join post on (channel.username = post.authorusername) join subscription on (channel.username = subscription.creatorusername)
WHERE country = 'Brazil'
GROUP BY channel.username;


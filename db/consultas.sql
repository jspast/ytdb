DROP VIEW VideoLikes;

-- Visão que representa o id de vídeo e o número de likes de vídeos
CREATE VIEW VideoLikes AS
SELECT Video.id, COUNT(*) AS VideoLikes
FROM Video NATURAL LEFT JOIN Visualization
WHERE Visualization.liked = TRUE
GROUP BY Video.id

-- Consulta que representa o maior número de likes em um vídeo por categoria que tem vídeos com likes
SELECT videocategory.name AS Category, max(videolikes) AS MaxLikes
FROM videocategory left join (videolikes natural join video) on (name = videocategoryname)
GROUP BY videocategory.name
HAVING max(videolikes) > 0;

-- Consulta que representa a quantidade total de likes por canal
SELECT channel.displayname, sum(videolikes) AS Likes
FROM (video natural join videolikes) join post on (video.postid = post.id) join channel on (post.authorusername = channel.username)
GROUP BY channel.username;

-- Consulta que representa id de vídeo de vídeos que receberam comentário menos de 1 minuto após a publicação
SELECT video.id
FROM video join post as videopost on (video.postid = videopost.id) join
    (comment join post as commentpost on (comment.postid = commentpost.id)) on (video.postid = comment.attachedpostid)
WHERE commentpost.date < videopost.date + '1 minute';

-- Consulta que representa os canais que postaram nenhum vídeo há menos de uma semana
SELECT channel.displayname
FROM channel join (video join post on (video.postid = post.id)) on (channel.username = post.authorusername)
EXCEPT
SELECT channel.displayname
FROM channel join (video join post on (video.postid = post.id)) on (channel.username = post.authorusername)
WHERE post.date > date_subtract(current_timestamp, '1 week');


CREATE VIEW VideoInteractions AS
SELECT Video.id, Video.title, VideoLikes, VideoDislikes
FROM Video
NATURAL LEFT JOIN (
    SELECT Video.id, COUNT(*) AS VideoLikes
    FROM Video NATURAL LEFT JOIN Visualization
    WHERE Visualization.liked = true
    GROUP BY Video.id
)
NATURAL LEFT JOIN (
    SELECT Video.id, COUNT(*) AS VideoDislikes
    FROM Video NATURAL LEFT JOIN Visualization
    WHERE Visualization.disliked = true
    GROUP BY Video.id
)

CREATE VIEW VideoLikes AS
SELECT Video.id, COUNT(*) AS VideoLikes
FROM Video NATURAL LEFT JOIN Visualization
WHERE Visualization.liked = true
GROUP BY Video.id
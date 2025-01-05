CREATE TABLE Account (
    email VARCHAR(256) PRIMARY KEY,
    isPremium BOOLEAN NOT NULL DEFAULT false,
    name VARCHAR(256) NOT NULL
);

CREATE TABLE Channel (
    about TEXT,
    displayName VARCHAR(50) NOT NULL,
    profilePicture BYTEA,
    isVerified BOOLEAN NOT NULL DEFAULT false,
    userName VARCHAR(256) PRIMARY KEY,
    creationDate TIMESTAMP NOT NULL,
    url VARCHAR NOT NULL,
    country VARCHAR(128) NOT NULL,
    accountEmail VARCHAR(256) NOT NULL REFERENCES Account(email) ON DELETE CASCADE
);

CREATE TABLE Post (
    id INT PRIMARY KEY,
    date TIMESTAMP NOT NULL,
    authorUserName VARCHAR(256) NOT NULL REFERENCES Channel(userName) ON DELETE CASCADE
);

CREATE TABLE VideoCategory (
    name VARCHAR(64) PRIMARY KEY,
    url VARCHAR NOT NULL
);

CREATE TABLE Video (
    title VARCHAR NOT NULL,
    allowComments BOOLEAN NOT NULL DEFAULT true,
    id CHAR(11) PRIMARY KEY,
    postId INT UNIQUE NOT NULL REFERENCES Post(id) ON DELETE CASCADE,
    lenght INT NOT NULL,
    thumbnail BYTEA NOT NULL,
    description VARCHAR(5000) NOT NULL,
    videoCategoryName VARCHAR(64) NOT NULL REFERENCES VideoCategory(name) ON DELETE RESTRICT
);

CREATE TABLE VideoImage (
    frameRate INT NOT NULL,
    codec VARCHAR(64) NOT NULL,
    isPremium BOOLEAN NOT NULL,
    bitRate INT NOT NULL,
    resolution VARCHAR(16) NOT NULL,
    data BYTEA NOT NULL,
    videoId CHAR(11) NOT NULL REFERENCES Video(id) ON DELETE CASCADE,
    PRIMARY KEY (frameRate, codec, bitRate, resolution)
);

CREATE TABLE VideoAudio (
    codec VARCHAR(64) NOT NULL,
    data BYTEA NOT NULL,
    bitRate INT NOT NULL,
    videoId CHAR(11) NOT NULL REFERENCES Video(id) ON DELETE CASCADE,
    PRIMARY KEY (codec, bitRate)
);

CREATE TABLE VideoChapter (
    endTime INT NOT NULL,
    startTime INT PRIMARY KEY,
    name VARCHAR(5000) NOT NULL,
    videoId CHAR(11) NOT NULL REFERENCES Video(id) ON DELETE CASCADE
);

CREATE TABLE Comment (
    id INT PRIMARY KEY,
    postId INT UNIQUE NOT NULL REFERENCES Post(id) ON DELETE CASCADE,
    attachedPostId INT NOT NULL REFERENCES Post(id) ON DELETE CASCADE,
    text VARCHAR(10000) NOT NULL
);

CREATE TABLE VideoSubtitle (
    data BYTEA NOT NULL,
    language VARCHAR(32),
    videoId CHAR(11) NOT NULL REFERENCES Video(id) ON DELETE CASCADE,
    PRIMARY KEY (language, videoId)
);

CREATE TABLE Media (
    image BYTEA,
    url VARCHAR PRIMARY KEY,
    name VARCHAR(128) NOT NULL
);

CREATE TABLE Tag (
    videoId CHAR(11) NOT NULL REFERENCES Video(id) ON DELETE CASCADE,
    tag VARCHAR,
    PRIMARY KEY (videoId, tag)
);

CREATE TABLE Subscription (
    spectatorUserName VARCHAR(256) NOT NULL REFERENCES Channel(userName) ON DELETE CASCADE,
    creatorUserName VARCHAR(256) NOT NULL REFERENCES Channel(userName) ON DELETE CASCADE,
    isBellOn BOOLEAN NOT NULL DEFAULT false,
    PRIMARY KEY (spectatorUserName, creatorUserName)
);

CREATE TABLE Presentation (
    videoId CHAR(11) NOT NULL REFERENCES Video(id) ON DELETE CASCADE,
    mediaUrl VARCHAR NOT NULL REFERENCES Media(url) ON DELETE CASCADE,
    PRIMARY KEY (videoId, mediaUrl)
);

CREATE TABLE Visualization (
    channelUserName VARCHAR(256) NOT NULL REFERENCES Channel(userName) ON DELETE CASCADE,
    postId INT NOT NULL REFERENCES Post(id) ON DELETE CASCADE,
    liked BOOLEAN NOT NULL DEFAULT false,
    disliked BOOLEAN NOT NULL DEFAULT false,
    PRIMARY KEY (channelUserName, postId)
);

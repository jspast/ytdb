INSERT INTO Account (email, ispremium, name)
VALUES
    ('leonmartins@gmail.com', true, 'Leon Martins'),
    ('jschlatt@gmail.com', false, 'Jebediah Schlatt');


INSERT INTO Channel (displayname, isverified, username, creationdate, url, country, accountemail)
VALUES
    ('Coisa de Nerd', true, 'coisadenerd', '2010-12-30 15:30:00', 'https://www.youtube.com/@coisadenerd', 'Brazil', 'leonmartins@gmail.com'),
    ('República Coisa de Nerd', true, 'republicacoisadenerd', '2018-12-30 15:30:00', 'https://www.youtube.com/@republicacoisadenerd', 'Brazil', 'leonmartins@gmail.com'),
    ('Cadê a Chave?', true, 'cadeachave', '2013-12-30 15:30:00', 'https://www.youtube.com/@cadeachave', 'Brazil', 'leonmartins@gmail.com'),
    ('Big guy', false, 'bigmanschlatt', '2020-12-30 15:30:00', 'https://www.youtube.com/@bigmanschlatt', 'United States', 'jschlatt@gmail.com'),
    ('jschlatt', true, 'jschlatt', '2013-12-30 15:30:00', 'https://www.youtube.com/@jschlatt', 'United States', 'jschlatt@gmail.com'),
    ('jschlattLIVE', true, 'jschlattLIVE', '2019-12-30 15:30:00', 'https://www.youtube.com/@jschlattLIVE', 'United States', 'jschlatt@gmail.com');

INSERT INTO VIDEOCATEGORY (name, url)
VALUES
    ('Autos & Vehicles', ''),
    ('Comedy', ''),
    ('Education', ''),
    ('Entertainment', ''),
    ('Film & Animation', ''),
    ('Gaming', ''),
    ('Howto & Style', ''),
    ('Music', ''),
    ('News & Politics', ''),
    ('Nonprofits & Activism', ''),
    ('People & Blogs', ''),
    ('Pets & Animals', '')

INSERT INTO VIDEO (title, allowcomments, id, postid, length, description, videocategoryname)
VALUES
    ('ELE COMPROU OS MELHORES PRESENTES - Ep.1722', true, 'f-ZeYC_EK-E', 3, 1200, '', 'Entertainment'),
    ('OS MELHORES PRESENTES DE NATAL!', true, 'or2H9xLYzSM', 2, 720, '', 'Entertainment'),
    ('Sobre esse PS5 Pro...', true, 'ojy7cvXN80c', 1, 900, '', 'Entertainment'),
    ('I made a Christmas album.', true, 'nYD0aYb37Zo', 6, 600, '', 'Entertainment'),
    ('A Tribute to Minecraft', true, 'zNZ1rq5kW4M', 5, 720, '', 'Gaming'),
    ('A message from Big guy', false, 'Ndy_wIlm7ns', 4, 25, '', 'Entertainment')

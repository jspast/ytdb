-- Função gatilho que ativa o sino de notificações com base em uma visualização
-- Se o canal que visualizou for inscrito no canal da publicação, o sino ativará
-- (O PostgreSQL não suporta o uso de procedimentos em gatilhos, apenas funcões.
--  Ver: https://www.postgresql.org/docs/17/sql-createtrigger.html)
CREATE OR REPLACE FUNCTION turnBellOn()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE subscription SET isbellon = true
    WHERE spectatorUserName = NEW.channelusername
    AND creatorUserName in
        (SELECT authorusername
        FROM visualization JOIN POST ON (visualization.POSTID = POST.ID)
        WHERE POST.ID = NEW.POSTID);
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Gatilho que ativa o sino de notificações quando um canal curte uma publicação
-- de um canal que é inscrito
CREATE OR REPLACE TRIGGER likeTurnsBellOn
AFTER INSERT OR UPDATE OF liked ON visualization
FOR EACH ROW
WHEN (NEW.liked = true)
EXECUTE FUNCTION turnBellOn();


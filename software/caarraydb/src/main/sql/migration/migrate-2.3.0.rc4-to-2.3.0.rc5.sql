-- GF 22810 (rolling back RC4 insert of GEO provider)
delete from contact where name='GEO' and provider = true and discriminator = 'O';
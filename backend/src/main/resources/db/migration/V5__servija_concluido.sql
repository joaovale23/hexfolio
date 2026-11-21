-- O ServiJá foi concluído, mas ainda não entrou em produção: a empresa priorizou outros sistemas.
update project set status = 'concluído' where slug = 'servija' and lang = 'pt';
update project set status = 'completed' where slug = 'servija' and lang = 'en';

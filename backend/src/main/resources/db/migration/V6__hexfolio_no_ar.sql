-- O hexfolio está publicado (front no GitHub Pages, API no Render): deixa de ser "em construção".
update project set status = 'no ar' where slug = 'hexfolio' and lang = 'pt';
update project set status = 'live' where slug = 'hexfolio' and lang = 'en';

-- Mesmo termo para projetos concluídos em inglês, como já é no ServiJá.
update project set status = 'completed' where slug = 'etl-ans' and lang = 'en';

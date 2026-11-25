-- Inglês passa a avançado, com a nota do TOEIC (910 de 990).
update education set institution = 'Português nativo · inglês avançado (TOEIC 910/990) · espanhol intermediário · francês básico'
 where lang = 'pt' and position = 3;
update education set institution = 'Portuguese (native) · English (advanced, TOEIC 910/990) · Spanish (intermediate) · French (basic)'
 where lang = 'en' and position = 3;

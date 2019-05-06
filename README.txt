Criar bases:
username: vsm_crm
password: Vsm_Crm_Admin_1!

Cliente:
vsm_crm_cliente_test
vsm_crm_cliente_dev

SSO
vsm_crm_sso_test


Criar Usuário:
http://localhost:8080/api/admin/user/create
Request:
{
	"id": null,
    "name": "Admin",
    "email": "admin@email.com.br",
    "admin": true,
    "login": "admin",
    "password": "admin",
    "active": true,
    "locked": false,
    "lockingReason": null
}

Autenticar:
http://localhost:8080/login
Request:
{
	"username": "admin",
	"password": "admin"
}

Incluir venda:
http://localhost:8081/api/v1/crm/venda/incluir
Request:
{
	"ocorrencia": "2019-05-05 13:54:00",
    "diaOcorrencia": "2019-05-05",
    "referenciaPdv": 12345678,
	"cliente": {
		"id": 1 
	},
    "valor": 10.58
}

Response:
{
    "message": "Pontos: 10",
    "status": "SUCCESS"
}

Para qualquer requisição, adicionar ao header:
Content-Type application/json
Authorization Bearer <token retornado da autenticação>

Retornar o histórico de vendas de um determinado período
Inicio e fim formato - yyyy-MM-dd
http://localhost:8081/api/crm/venda/filtro/periodo/{inicio}/{fim}

http://localhost:8081/api/crm/venda/filtro/periodo/2019-05-05/2019-05-05
Response:
{
    "vendas": [
        {
            "id": 13,
            "ocorrencia": "2019-05-05 20:35:01",
            "diaOcorrencia": "2019-05-05",
            "referenciaPdv": 513976951284842195,
            "cliente": {
                "id": 2,
                "nome": "Mr da Silva Sauro",
                "idade": 28,
                "genero": "M",
                "cpf": "111.222.333-44",
                "email": "cliente@email.com.br",
                "endereco": {
                    "id": 1,
                    "rua": "Rua dos Sauros, 123",
                    "cidade": "Cidade",
                    "estado": "SP",
                    "cep": "12345-678"
                }
            },
            "valor": 10.8,
            "pontuacao": {
                "id": 13,
                "ocorrencia": "2019-05-05 20:35:01",
                "regraPontuacao": {
                    "id": 3,
                    "valorInicio": 10.71,
                    "valorFim": 10.8,
                    "inicio": "2019-05-04",
                    "fim": "2019-05-06",
                    "pontos": 30
                }
            }
        },
        {
            "id": 14,
            "ocorrencia": "2019-05-05 20:35:01",
            "diaOcorrencia": "2019-05-05",
            "referenciaPdv": -1889634845925284179,
            "cliente": {
                "id": 2,
                "nome": "Mr da Silva Sauro",
                "idade": 28,
                "genero": "M",
                "cpf": "111.222.333-44",
                "email": "cliente@email.com.br",
                "endereco": {
                    "id": 1,
                    "rua": "Rua dos Sauros, 123",
                    "cidade": "Cidade",
                    "estado": "SP",
                    "cep": "12345-678"
                }
            },
            "valor": 10.8,
            "pontuacao": {
                "id": 14,
                "ocorrencia": "2019-05-05 20:35:01",
                "regraPontuacao": {
                    "id": 3,
                    "valorInicio": 10.71,
                    "valorFim": 10.8,
                    "inicio": "2019-05-04",
                    "fim": "2019-05-06",
                    "pontos": 30
                }
            }
        },
        {
            "id": 15,
            "ocorrencia": "2019-05-05 20:35:01",
            "diaOcorrencia": "2019-05-05",
            "referenciaPdv": -8751379644711338082,
            "cliente": {
                "id": 2,
                "nome": "Mr da Silva Sauro",
                "idade": 28,
                "genero": "M",
                "cpf": "111.222.333-44",
                "email": "cliente@email.com.br",
                "endereco": {
                    "id": 1,
                    "rua": "Rua dos Sauros, 123",
                    "cidade": "Cidade",
                    "estado": "SP",
                    "cep": "12345-678"
                }
            },
            "valor": 10.8,
            "pontuacao": {
                "id": 15,
                "ocorrencia": "2019-05-05 20:35:01",
                "regraPontuacao": {
                    "id": 3,
                    "valorInicio": 10.71,
                    "valorFim": 10.8,
                    "inicio": "2019-05-04",
                    "fim": "2019-05-06",
                    "pontos": 30
                }
            }
        },
        {
            "id": 16,
            "ocorrencia": "2019-05-05 20:35:01",
            "diaOcorrencia": "2019-05-05",
            "referenciaPdv": -2559688724799951675,
            "cliente": {
                "id": 2,
                "nome": "Mr da Silva Sauro",
                "idade": 28,
                "genero": "M",
                "cpf": "111.222.333-44",
                "email": "cliente@email.com.br",
                "endereco": {
                    "id": 1,
                    "rua": "Rua dos Sauros, 123",
                    "cidade": "Cidade",
                    "estado": "SP",
                    "cep": "12345-678"
                }
            },
            "valor": 10.8,
            "pontuacao": {
                "id": 16,
                "ocorrencia": "2019-05-05 20:35:01",
                "regraPontuacao": {
                    "id": 3,
                    "valorInicio": 10.71,
                    "valorFim": 10.8,
                    "inicio": "2019-05-04",
                    "fim": "2019-05-06",
                    "pontos": 30
                }
            }
        },
        {
            "id": 17,
            "ocorrencia": "2019-05-05 13:54:00",
            "diaOcorrencia": "2019-05-05",
            "referenciaPdv": 12345678,
            "cliente": {
                "id": 1,
                "nome": "Mrs da Silva Sauro",
                "idade": 28,
                "genero": "F",
                "cpf": "111.222.333-44",
                "email": "cliente@email.com.br",
                "endereco": {
                    "id": 1,
                    "rua": "Rua dos Sauros, 123",
                    "cidade": "Cidade",
                    "estado": "SP",
                    "cep": "12345-678"
                }
            },
            "valor": 10.58,
            "pontuacao": {
                "id": 17,
                "ocorrencia": "2019-05-05 13:54:00",
                "regraPontuacao": {
                    "id": 1,
                    "valorInicio": 10.5,
                    "valorFim": 10.6,
                    "inicio": "2019-05-04",
                    "fim": "2019-05-06",
                    "pontos": 10
                }
            }
        }
    ],
    "quantidade": null
}

Retornar a quantidade vendas por sexo de um determinado período
Inicio e fim formato - yyyy-MM-dd
http://localhost:8081/api/crm/venda/filtro/quantidadevendas/groupedbygenero/{inicio}/{fim}

http://localhost:8081/api/crm/venda/filtro/periodo/2019-05-05/2019-05-05
Response:
{
    "vendas": null,
    "quantidade": {
        "F": 1,
        "M": 4
    }
}

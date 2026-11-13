# Pizzaria, Dashboard de Pedidos

Dashboard administrativo para acompanhar os pedidos de uma pizzaria, com login seguro, controle de acesso por perfil e uma API REST que alimenta os indicadores e gráficos. Feito com Java Spring Boot, Spring Security, Thymeleaf, Chart.js e MongoDB Atlas.

O projeto reaproveita a estrutura do sistema de Login Seguro: a autenticação, os perfis, o layout e os temas foram mantidos, e o domínio de pedidos foi acrescentado por cima.

## Funcionalidades

* Cadastro de usuários com validação dos dados
* Senhas armazenadas com hash BCrypt
* Login e logout com Spring Security
* Três perfis de acesso: `ADMIN`, `GESTOR` e `FUNCIONARIO`
* Usuários, sessões e pedidos armazenados no MongoDB Atlas
* API REST de pedidos e de indicadores
* Dashboard com indicadores, gráficos, ranking e filtros
* Tema visual configurável

## Perfis e permissões

| Página | Rota | FUNCIONARIO | GESTOR | ADMIN |
|---|---|---|---|---|
| Início | `/` | Sim | Sim | Sim |
| Painel de pedidos | `/gestor/dashboard` | Não | Sim | Sim |
| Usuários | `/admin/usuarios` | Não | Não | Sim |
| API | `/api/**` | Não | Sim | Sim |

Todo usuário que se cadastra recebe o perfil `FUNCIONARIO`. O administrador pode alterar o perfil ou excluir usuários na página Usuários.

## Painel de pedidos

* Indicadores gerais: total de pedidos, em preparo, entregues, cancelados, faturamento e ticket médio
* Gráfico de faturamento por período
* Gráfico da situação dos pedidos
* Ranking dos sabores mais vendidos
* Tabela com os pedidos e as observações
* Filtros por período, status, sabor e atendente
* Atualização pelo botão Atualizar e automática a cada minuto

A página carrega os dados chamando a API por JavaScript.

Valores usados nos pedidos:

| Campo | Valores |
|---|---|
| Status | Recebido, Em preparo, Entregue, Cancelado |
| Sabor | Calabresa, Mussarela, Portuguesa, Frango com catupiry |

## API

Todos os endpoints de consulta aceitam os filtros opcionais `inicio` e `fim` (data do pedido, formato `AAAA-MM-DD`), `status`, `sabor` e `atendente`.

| Método | Endpoint | Descrição |
|---|---|---|
| GET | `/api/pedidos` | Lista os pedidos |
| GET | `/api/pedidos/{id}` | Busca um pedido |
| POST | `/api/pedidos` | Cadastra um pedido |
| PUT | `/api/pedidos/{id}` | Atualiza um pedido |
| GET | `/api/dashboard/indicadores` | Indicadores gerais |
| GET | `/api/dashboard/faturamento-por-periodo` | Faturamento dos pedidos entregues por data |
| GET | `/api/dashboard/status` | Quantidade de pedidos por status |
| GET | `/api/dashboard/sabores` | Ranking dos sabores |

Exemplo de corpo para cadastro:

```json
{
  "cliente": "Ana",
  "descricao": "Pizza grande",
  "sabor": "Calabresa",
  "status": "Recebido",
  "valor": 59.90,
  "atendente": "Bruno"
}
```

Possíveis erros:

| Código | Quando acontece |
|---|---|
| 400 | Campo obrigatório vazio, valor negativo, JSON inválido ou data fora do formato |
| 401 | Requisição sem autenticação |
| 403 | Usuário sem perfil `GESTOR` ou `ADMIN` |
| 404 | Pedido não encontrado |

Fora do navegador (por exemplo, no Postman), a API aceita autenticação Basic com o e-mail e a senha de um usuário `GESTOR` ou `ADMIN`.

## Estrutura do projeto

```
src/main/java/com/pizzaria
├── config        Segurança, tema, administrador e pedidos iniciais
├── controller    Páginas e endpoints da API
├── entity        Usuario, Perfil e Pedido
├── repository    Acesso ao MongoDB
└── service       Regras de negócio, autenticação e indicadores

src/main/resources
├── application.properties
├── static
│   ├── css
│   │   ├── style.css         Estilos das páginas
│   │   └── temas             Um arquivo de cores por tema
│   └── js/dashboard.js       Integração do painel com a API
└── templates
    ├── fragments/layout.html   Cabeçalho e menu reaproveitados
    ├── login.html
    ├── cadastro.html
    ├── home.html
    ├── acesso-negado.html
    ├── admin/usuarios.html
    └── gestor/dashboard.html
```

## Pré-requisitos

* Java 17 ou superior
* Conta gratuita no [MongoDB Atlas](https://www.mongodb.com/products/platform)
* Git

Não é necessário instalar o Maven, o projeto já inclui o Maven Wrapper (`mvnw`).

## Configuração do MongoDB Atlas

1. Crie um cluster gratuito (M0) no MongoDB Atlas.
2. Em **Database Access**, crie um usuário de banco com senha.
3. Em **Network Access**, adicione o seu endereço IP.
4. Em **Database > Connect > Drivers**, copie a string de conexão.

Clusters gratuitos são pausados após um período sem uso. Se o sistema não conectar, confira no Atlas se o cluster está ativo e se o seu IP atual está liberado.

## Arquivo de configuração

A string de conexão não fica no código. Ela é lida de um arquivo `.env`, que não é enviado ao GitHub.

1. Faça uma cópia do arquivo `.env.example` com o nome `.env`.
2. Preencha os valores:

```
MONGODB_URI=mongodb+srv://USUARIO:SENHA@SEU_CLUSTER.mongodb.net/?retryWrites=true&w=majority
ADMIN_EMAIL=admin@pizzaria.com
ADMIN_SENHA=uma-senha-forte
```

`ADMIN_EMAIL` e `ADMIN_SENHA` são usados para criar o primeiro administrador quando o sistema inicia pela primeira vez.

## Como executar

No Windows:

```
mvnw.cmd spring-boot:run
```

No Linux ou macOS:

```
./mvnw spring-boot:run
```

Depois acesse http://localhost:8080 e entre com o e-mail e a senha do administrador definidos no `.env`.

## Integração com o MongoDB Atlas

A conexão é configurada no `application.properties` pela propriedade `spring.data.mongodb.uri`, que recebe o valor de `MONGODB_URI`. O banco usado é o `pizzaria`, com três coleções:

| Coleção | Conteúdo | Quem grava |
|---|---|---|
| `usuarios` | Nome, e-mail, senha com hash e perfil | `UsuarioRepository` (Spring Data MongoDB) |
| `sessoes` | Sessões dos usuários autenticados | Spring Session MongoDB |
| `pedidos` | Cliente, descrição, sabor, status, valor, atendente, datas e observação | `PedidoRepository` (Spring Data MongoDB) |

As coleções são criadas automaticamente na primeira execução. Se a coleção `pedidos` estiver vazia, o sistema cadastra dois pedidos de exemplo.

## Como trocar o tema

Cada tema é um arquivo em `src/main/resources/static/css/temas` com as variáveis de cor do sistema. Para trocar, altere a propriedade no `application.properties`:

```
app.tema=escuro
```

Para criar um novo tema, copie o arquivo `padrao.css`, mude as cores e informe o nome do novo arquivo em `app.tema`. O nome exibido no sistema é definido em `app.nome`.

## Fluxo de branches

O projeto segue o Gitflow: `main` para as versões entregues, `develop` para a integração e branches `feature/*` para cada funcionalidade.

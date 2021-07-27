# Backlog-CICD-cooperation

[日本語版 README はこちら](https://nulab-exam.backlog.jp/git/YANO/app/blob/main/README-ja.md)

When you change the status of a particular issue in your Backlog, the CircleCI pipeline runs in tandem.

## Demo

![demo](https://nulab-exam.backlog.jp/git/YANO/app/raw/main/demo/demo.gif)

## What is Backlog-CICD-cooperation?

" I'm done with the test. But I forgot to change the status of the Backlog task. "  
" I'm not sure if I'm really done testing this task. "

The objective of the system is to synchronize project management tasks with reality.

## Use cases

- Change the status of the unit test task in Backlog. Then, CircleCI will execute the test process.
- Change the status of the deploy task in Backlog. Then, CircleCI will execute the deploy process.

## Design documentations

- [System architecture diagram](https://cacoo.com/diagrams/JbFA6UR4chm083qo/5D359?reload_rt=1625905817698_0)
- [Sequence diagram](https://cacoo.com/diagrams/JbFA6UR4chm083qo/4E1D2?reload_rt=1625905817698_0)
- [Class diagram](https://cacoo.com/diagrams/JbFA6UR4chm083qo/5B77F?reload_rt=1626841513076_0)
- [DB-API documentations](https://docs.google.com/spreadsheets/d/1COsu1uTUe9xB2TvbY62HldaVBebx5qDsnjZVuvT1_kw/edit?usp=sharing)

## Usage

### Preparation

- Issue Backlog API key（ references : [API Settings - Backlog Help Center](https://support.backlog.com/hc/en-us/articles/115015420567-API-Settings) ）
- Issue Circle API key（ references : [Managing API Tokens - CircleCI](https://circleci.com/docs/2.0/managing-api-tokens/) ）
- To link Github and CircleCI.

### Work procedure

1. [ /login" ] - Login with the Backlog SpaceID and APIkey.
2. [ /issues (POST) ] - Save a trigger data. (Information Backlog and CircleCI)
5. [ /issues (GET) ] - Check UUID.
6. [ /webhook/:uuid ] - Specify in webhook using Backlog.
7. CircleCI is executed when the registered trigger data and the update match.

The details of the designation method are as follows.

### Endpoint

https://backlog-cicd-cooperation.herokuapp.com

| URL | Method  |  Detail  |
| --- | ---- | ---- |
| /login | POST | Login with the Backlog SpaceID and APIkey  |
| /logout | GET | Logout|
| /issues | GET | Display the list of trigger data for logged in user. |
| /issues | POST | Add the trigger data for logged in user.  |
| /issues/:id | DELETE | Delete the Specified trigger data for logged in user. |
| /webhook/:uuid | POST  | Execution trigger for A |

#### /login (POST)

You can login by sending your Backlog space key and API key. if the login is successful, a cookie(JWT Authentication)
will be saved.

```json
{
  "backlogSpacekey": "[Spacekey]",
  "backlogApikey": "[Backlog API Key]"
}
```

#### /logout (GET)

if cookies exist, delete them.

#### /issues (GET)

The list of trigger data for the logged-in user will be displayed. If you are not logged in, an error message will be
displayed.

#### /issues (POST)

Save the trigger data describing the execution conditions.

```json
{
  "backlogIssuekey": "XXX-1",
  "backlogStatus": "3",
  "circleciUsername": "[Username]",
  "circleciRepository": "[Repository]",
  "circleciPipeline": "[Branch]",
  "circleciApikey": "[CircleCI API key]"
}
```

| Item | Detail  |  Example  |
| --- | ---- | ---- |
| backlogIssuekey    | Specify the Backlog task. ( Combine the project key and keyID with a hyphen. ) | ABC-7  |
| backlogStatus      | Specify the status of a task by its number. | 3 |
| circleciUsername   | Specify the Github user name. | akiunleash   |
| circleciRepository | Specify the Github repository. | myripository |
| circleciPipeline   | Specify the CircleCI pipeline. | main  |
| circleciApikey     | Specify the CircleCI API key. | sidxl928ox....  |

#### /issues/:id (DELETE)

Delete the trigger data for the ID specified in the URL.
(Only data from logged-in accounts.)

#### /webhook/:uuid

- Specify this URL in the webhook settings of Backlog.
- Specifies the UUID displayed by [ /issues (get) ]
- Configure Backlog to run a webhook when an issue is updated.

## Note

### Setting up the development environment

Add the config file to [ /src/main/resources/application.conf ]

```
mysql = {
  dataSourceClass="com.mysql.cj.jdbc.MysqlDataSource"
  properties {
    user="[user]"
    password="[password]"
    databaseName="[dbname]"
    serverName="localhost"
    portNumber="3306"
  }
  numThreads=10
}

auth {
    cookieName = "[Cookie name]"
    securityKey = "[Security key]"
}

test {
    nomalSpacekey = "[Nomal spacekey]"
    nomalApikey = "[Nomal apikey]"
    nonNomalSpacekey = "[Non-Nomal spacekey]"
    nonNomalApikey = "[Non-Nomal apikey]"
}
```

You can create a Docker image with the following command.

```shell
sbt docker:publishLocal
```

## Current Status and future
### Regarding Unit tests
The testing was done in the manner described in the UnitTest section of the [DB-API documentations](https://docs.google.com/spreadsheets/d/1COsu1uTUe9xB2TvbY62HldaVBebx5qDsnjZVuvT1_kw/edit?usp=sharing) (Using Postman)
Only part of the process is automated.(Using Akka testkit)
it has been checked using Backlog-CICD-cooperation.

### Regarding log
The server administrator can view the log with the following command.
```shell
heroku logs -n 1500
```

It only shows up to 1500 records, so I think this point needs to be improved.

### Regarding CircleCI process
Currently, only one process can be handled.
I think it needs to be improved to be able to handle multiple processes.

## Licence

[MIT](https://github.com/tcnksm/tool/blob/master/LICENCE)

## Author

[Akio Yano](https://github.com/AkiUnleash)

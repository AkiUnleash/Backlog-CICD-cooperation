# Backlog-CICD-cooperation

[English Readme is here](https://nulab-exam.backlog.jp/git/YANO/app/blob/main/README.md)

Backlogの特定タスクの状況を変更することで、CircleCIが連動して実行されるシステムです。

## デモ

![demo](https://nulab-exam.backlog.jp/git/YANO/app/raw/main/demo/demo.gif)

## Backlog-CICD-cooperation って何？

「テストは完了したけど、Backlogのタスクを完了済みにするのを忘れていた。」  
「あとになって、このタスクのテストが本当に終わったのかがわからない。」

プロジェクト管理のタスクと、実際のタスクを同期することを目的としてこのシステムを作成しました。  

## 使用例

- Backlogに登録している「ユニットテスト」タスクの状況を処理済みに変更すると、CircleCIが実行されテストを行う。
- Backlogに登録している「デプロイ」タスクの状況を処理済みに変更すると、CircleCIが実行されデプロイを行う。

## 設計書

- [システム・アーキテクチャ図](https://cacoo.com/diagrams/JbFA6UR4chm083qo/5D359?reload_rt=1625905817698_0)
- [シーケンス図](https://cacoo.com/diagrams/JbFA6UR4chm083qo/4E1D2?reload_rt=1625905817698_0)
- [クラス図](https://cacoo.com/diagrams/JbFA6UR4chm083qo/5B77F?reload_rt=1626841513076_0)
- [DB-API設計書](https://docs.google.com/spreadsheets/d/1COsu1uTUe9xB2TvbY62HldaVBebx5qDsnjZVuvT1_kw/edit?usp=sharing)

## 使い方

### 準備

- BacklogのAPIキーを発行（参考：[APIの設定 – Backlog ヘルプセンター](https://support-ja.backlog.com/hc/ja/articles/360035641754-API%E3%81%AE%E8%A8%AD%E5%AE%9A) ）
- CircleCIのAPIキーを発行（参考：[API トークンの管理 – Circle CI](https://circleci.com/docs/ja/2.0/managing-api-tokens/) ）
- GithubとCircleCIをアカウント連携させ、実行可能状態にしておく。

### 手順

1. [ /login ] にて、BacklogのSpaceID及びAPIキーを使いログインします。
2. [ /issues (POST) ] にて、トリガーデータ（Backlog及びCircleCIの情報）を保存してください。
3. [ /issues (GET) ]にて 、UUIDを確認してください。
4. [ /webhook/:uuid ]を、BacklogのWebhookに指定してください。
5. Backlog上タスクの状況を指定したトリガーと一致する更新を行うと、CircleCIが自動実行されます。

指定方法の詳細は、以下エントリーポイントにて説明しています。

### エンドポイント

https://backlog-cicd-cooperation.herokuapp.com

| URL | メソッド  |  内容  |
| --- | ---- | ---- |
| /login | POST | BacklogのスペースIDとAPIキーでログイン処理  |
| /logout | GET | ログアウト処理  |
| /issues | GET | ログインユーザーの実行トリガーを一覧表示  |
| /issues | POST | ログインユーザーの実行トリガーを登録  |
| /issues/:id | DELETE | ログインユーザーの指定した実行トリガーを削除  |
| /webhook/:uuid | POST  |  CircleCIの実行トリガー  |

#### /login (POST)

BacklogのスペースキーとAPIキーを送信することで、ログインが可能です。  
ログインが成功すると、JWT認証(Cookie)が保存されます。

```json
{
  "backlogSpacekey": "[Spacekey]",
  "backlogApikey": "[Backlog API Key]"
}
```

#### /logout (GET)

Cookieが存在している場合、そのCookieを削除します。

#### /issues (GET)

ログインしているアカウントの、トリガー一覧が表示されます。  
ログインしていない場合は、エラーが返却されます。

#### /issues (POST)

Backlogのタスクがどの状況に変わったときに、どのCircleCIを実行するか。トリガーとなるデータを保存します。

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

| 項目 | 内容  |  例  |
| --- | ---- | ---- |
| backlogIssuekey    | Backlogのタスクの指定（プロジェクトキーとキーIDをハイフンで結合 ） | ABC-7  |
| backlogStatus      | Backlogのタスク状況を数値で指定 | 3 |
| circleciUsername   | Githubのユーザー名を指定 | akiunleash   |
| circleciRepository | Githubのリポジトリを指定 | myripository |
| circleciPipeline   | CircleCIのパイプラインを指定 | main  |
| circleciApikey     | CircleCIのAPIキーを指定 | sidxl928ox....  |

#### /issues/:id (DELETE)

パスで指定したIDの、トリガーデータを削除します(ログインしたアカウントのデータに限る)。

#### /webhook/:uuid

- BacklogのWebhook設定にて、このURLを指定します。
- [ :uuid ]は、[ /issues (GET) ]で表示されるUUIDを指定してください。
- Backlog側のWebhook設定は、課題の更新時にWebhookが動作するよう指定してください。


## 補足

### 開発環境の設定について  

 [ /src/main/resources/application.conf ] にConfigファイルを追加してください。

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

以下のコマンドをプロジェクトのディレクトリで実行すると、Dockerイメージ化することが出来ます。

```shell
sbt docker:publishLocal
```

## 現状と今後の構想
### ユニットテストについて 
[DB-API設計書](https://docs.google.com/spreadsheets/d/1COsu1uTUe9xB2TvbY62HldaVBebx5qDsnjZVuvT1_kw/edit?usp=sharing) のUnitTestシートに記載されている内容でPostmanを使い行いました。 数項目ですが、Akka Testkit を使用し自動化しています。実際にBacklog-CICD-Cooperationを使用して成功を確認しました。

### ログついて

"ch.qos.logback"を入れているため、サーバー管理者は以下のコマンドで閲覧できます。

```shell
heroku logs -n 1500
```

しかし現状では最大１５００レコードまでしか閲覧できないため、今後改善が必要と考えています。

### CircleCIの処理について
現状は１つのパイプラインに１つの処理しか出来ません。  
CircleCI(config.yml)の記載方法と実行を促すトリガーのパラメータを追加することで、処理を分岐することが可能なため対応していきたいと考えています。

## ライセンス

[MIT](https://github.com/tcnksm/tool/blob/master/LICENCE)

## Author

[Akio Yano](https://github.com/AkiUnleash)

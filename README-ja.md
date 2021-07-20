# Backlog-CICD-Collaboration

Backlogの特定タスクの状況を変更することで、CircleCIが連動して実行されるシステムです。  
[English Readme is here](https://github.com/AkiUnleash/Backlog-CICD-cooperation/blob/main/README.md)

## デモ
![demo](/demo/demo.gif)


## 設計書

- [システム・アーキテクチャ図](https://cacoo.com/diagrams/JbFA6UR4chm083qo/5D359?reload_rt=1625905817698_0)
- [シーケンス図](https://cacoo.com/diagrams/JbFA6UR4chm083qo/4E1D2?reload_rt=1625905817698_0)
- [DB-API設計書](https://docs.google.com/spreadsheets/d/1COsu1uTUe9xB2TvbY62HldaVBebx5qDsnjZVuvT1_kw/edit?usp=sharing)


## 使い方
### 準備
- BacklogのAPIキーを発行（参考：[APIの設定 – Backlog ヘルプセンター](https://support-ja.backlog.com/hc/ja/articles/360035641754-API%E3%81%AE%E8%A8%AD%E5%AE%9A) ）
- CircleCIのAPIキーを発行（参考：[API トークンの管理 – Circle CI](https://circleci.com/docs/ja/2.0/managing-api-tokens/) ）
- GithubとCircleCIをアカウント連携させ、実行可能状態にしておく。

### 手順

1. [ /login ] にて、BacklogのSpaceID及びAPIキーを使いログインします。
2. [ /issues (POST) ] にて、Backlog及びCercleCIの情報を保存してください。
3. [ /issues (GET) ]にて 、登録されたデータのUUIDを確認してください。
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
 | /webhook/:uuid | POST  |  CircleCIの実行トリガー確認  |

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

以下のコマンドをプロジェクトのディレクトリで実行すると、Dockerイメージ化することが出来ます。

```shell
sbt docker:publishLocal
```


## ライセンス

[MIT](https://github.com/tcnksm/tool/blob/master/LICENCE)

## Author

[Akio Yano](https://github.com/AkiUnleash)

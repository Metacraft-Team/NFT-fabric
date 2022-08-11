
![meta_painting](https://user-images.githubusercontent.com/5381613/184212725-096ed070-bb32-4e45-bdf7-311b73f048d5.png)

# NFT-fabric
[![Website](https://img.shields.io/website?down_message=offline&style=for-the-badge&up_color=blue&up_message=online&url=https%3A%2F%2Fmetacraft.cc)](https://metacraft.cc)
[![license](https://img.shields.io/github/license/Metacraft-Team/NFT-fabric?style=for-the-badge)](https://github.com/Metacraft-Team/NFT-fabric/blob/master/LICENSE)
[![Discord](https://img.shields.io/discord/881890111644631122?label=Discord&style=for-the-badge)](http://discord.gg/yEv3qKhVBH)
[![Twitter Follow](https://img.shields.io/twitter/follow/MetacraftCC?color=green&logoColor=green&style=for-the-badge&label=Twitter)](https://twitter.com/MetacraftCC)
[![YouTube Channel Views](https://img.shields.io/youtube/channel/views/UC-fAgQr5lxNVZU4_LVXmKOg?style=for-the-badge&label=Youtube%20Views)](https://www.youtube.com/channel/UC-fAgQr5lxNVZU4_LVXmKOg)

## Introduction

NFT-fabric is a Minecraft mod for Fabric 1.18.1 which lets you connect to the blockchain and take NFTs into Minecraft.

## How to use

Press "N" in-game to view the NFTs you own.
Left click on any NFT, you will get the item of the NFT.

![image](https://user-images.githubusercontent.com/5381613/183366156-7e9e99ab-91b4-4ae4-89b2-19ae0dc1c72f.png)

![image](https://user-images.githubusercontent.com/5381613/183366179-d3571501-b3c2-4e50-822e-dfe1dba87612.png)

Hold this NFT item, right-click on any wall, and the NFT will be hung on the wall. All players can appreciate your NFTs.

![image](https://user-images.githubusercontent.com/5381613/183366200-6651204c-29b3-4e20-8adc-817820125c72.png)

Isn't it really cool!

## Setup

You need to implement the interface of the server and configure the address in config.

```
./run/config/metacraft/metacraft_config.json
```

```
{
  "api_url": "http://127.0.0.1:8080"
}
```

### Server Interface
You need to implement the following server APIs

#### 1. Query NFT list by user UUID

| url       | method |
| :-------- | :------ | 
| /nft/list | GET     | 

##### params

| param       | type   | required | comment           |
| ---------- | ------ | -------- | ------------------ |
| uuid       | string | true     | Minecraft user UUID|
| collection | string | false     | collection name     |
| name       | string | false     | token name       |
| search     | string | false     | fuzzy seeach collection or name |
| ps         | int    | false    | page size | 
| pn         | int    | false    | page  number |

##### return data

```json
{
  "collections":[
    {
      "name": "Devs for Revolution",
      "img_url": "https://lh3.googleusercontent.com/6Jbode0t_bTO9MHYoYvjIW9nHENCxOs40EGg3Z5ptg4lLlD2z2WXEAIrjyV929aQnIi94hPL4VZ3Pl2NWOO_tSaO6gdjdrcMHrF9=s120"
    }
  ],
  "nft_list": [
    {
      "contract_address": "0x495f947276749ce646f68ac8c248420045cb7b5e",
      "token_id": "49060976528632914668253920865615404791469418896301597540949926131673459589121",
      "name": "OpenSea Collection",
      "symbol": "OPENSTORE",
      "image_url": "https://img.seadn.io/files/284aedcaec8982d2d807412944183ca7.png?fit=max&w=600",
      "permalink": "https://opensea.io/assets/0x13178ab07a88f065efe6d06089a6e6ab55ae8a15/187",
      "collection": {
        "name": "Devs",
        "img_url": "https://lh3.googleusercontent.com/6Jbode0t_bTO9MHYoYvjIW9nHENCxOs40EGg3Z5ptg4lLlD2z2WXEAIrjyV929aQnIi94hPL4VZ3Pl2NWOO_tSaO6gdjdrcMHrF9=s120"
      }
    }
  ],
  "page": {
    "total": 3,
    "ps": 2,
    "pn": 3
  }
}
```

###### return data explain

| param                     | type   | comment              |
| ------------------------- | ------ | ------------------ |
| collections.name          | string | collection name     |
| collections.img_url       | string | collection image |
| nft_list.contract_address | string | contract address           |
| nft_list.token_id         | string | token id           |
| nft_list.name             | string | nft name              |
| nft_list.symbol           | string | nft symbol               |
| nft_list.image_url        | string | token image           |
| nft_list.permalink        | string | opensea link        |
| page.total                | int    | total num   |
| page.ps                   | int    | page size           |
| page.pn                   | int    |page number            |

-------

#### 2. Check user own NFT

| url       | method |
| :-------- | :------ | 
| /nft/check | GET    | 


######  params

| param       | type   | required | comment           |
| ------- | ------ | -------- | -------- |
| uuid | string | true     | Minecraft user UUID |
| contract_address | string | true     | contract address |
| token_id | string | true     | NFT token id |


###### return data

```json
{"is_owner": true}
```

###### return data explain

| param       | type   |  comment           |
| ----------- | ---- | --------------------------- |
| is_owner | bool | true: own  false: not own |

## Issues and Feedback

If you have any questions, you can join our [Discord server](http://discord.gg/yEv3qKhVBH) and ask for help in the support channels.

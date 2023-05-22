# reversi

A simple network reversi game written in Swing.

# Features

- Swing製のネットワーク対戦リバーシゲーム
- クライアントサーバ型のネットワーク対戦モード
- ローカル上で動作するコンピュータ対戦モード(3段階のCPUレベル)
- IPアドレス指定によるクライアントブロック機能(block_list.txtにIPアドレスを改行区切りで記述)

# Demo

<p align="center">
  <img src="https://github.com/ck9/reversi/assets/47718193/083b6799-6bcf-4514-858d-415cc5bf98f6" />
</p>

# Installation

```bash
git clone https://github.com/ck9/reversi
cd reversi
make
```

# Usage

```bash
java -jar reversi-server.jar [<port>]
java -jar reversi-client.jar [<port>]
```

# Author

[ck9](https://github.com/ck9), [klyn0101](https://github.com/klyn0101), [nAmBuCde](https://github.com/nAmBuCde), [jkep-taba](https://github.com/jkep-taba)
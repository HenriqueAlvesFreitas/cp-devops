
### Perfil

## Cadastro com Autenticação e Token ╹Usuário╷ **`/registrar`**:

#### POST ➡️

**Exemplo 👇**
```js
{
     "nome": "Henrique Freitas",
	 "telefone": "11 959540882",
	 "email": "haf@gmail.com",
	 "login": "haf123",
	 "senha" : "02072004"
}
```
**Saída 👇**

|  | <font color="#aa31f5">código</font> | <font color="#e0af0d">descrição</font> |
|:------:|:------:|-----------|
✔️ | `201` | Usuário cadastrado com sucesso.
❌ | `403` | Não foi possível cadastrar o usuário.


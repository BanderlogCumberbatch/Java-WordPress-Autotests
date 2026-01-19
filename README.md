# Инструкция
В директории src/test/resources создать файл secrets.properties со следующими параметрами:
* api.username - логин для авторизации на WordPress
* api.password - пароль для авторизации на WordPress


# Тест-сьют REST API Wordpress

Тест-кейс 1. Создание поста: 

Предусловия: В запросе использован Basic Auth c корректным логином и паролем

Шаги:

Выполнить POST запрос: 
http://localhost:8000/index.php?rest_route=/wp/v2/posts с телом запроса: JSON

    {
        "status": "publish",
        "title": "New post",
        "content": "<sample text>"
    }

Ожидаемый результат: HTTP 201, JSON 

    {
        "id": целое число,
        "status": "publish",
        "title": { "rendered": "New post" },
        "content": { "rendered": "<p>sample text</p>\n" }
    }


Тест-кейс 2. Изменение поста: 

Предусловия: Существует пост "id" = {post_id} с "title" != "New new post", в запросе использован Basic Auth c корректным логином и паролем

Шаги:

Выполнить PATCH запрос: 
http://localhost:8000/index.php?rest_route=/wp/v2/posts/{id} с телом запроса: JSON

    {
       "title": "New new post"
    }

Ожидаемый результат: HTTP 200, JSON

    {
       "id": {post_id}
       "title": { "rendered": "New new post" }
    }


Тест-кейс 3. Удаление поста: 

Предусловия: Существует пост "id" = {post_id}, в запросе использован Basic Auth c корректным логином и паролем

Шаги:

Выполнить DELETE запрос: http://localhost:8000/index.php?rest_route=/wp/v2/posts/{id}&force=true

Ожидаемый результат: HTTP 200, JSON

    {
      "deleted": true,
      "previous": { "id": {post_id} }
    }


Тест-кейс 4. Создание комментария: 

Предусловия: Существует пост "id" = {post_id}, в запросе использован Basic Auth c корректным логином и паролем

Шаги:

Выполнить POST запрос: 
http://localhost:8000/index.php?rest_route=/wp/v2/comments с телом запроса: JSON 

    {
        "post": {post_id},
        "author_name": "Anon",
        "author_email": "unowen@unknown.com",
        "content": "sample content"
    }

Ожидаемый результат: HTTP 201, JSON

    {
        "id": целое число,
        "post": {post_id},
        "author_name": "Anon",
        "author_email": "unowen@unknown.com",
        "content": { "rendered": "<p>sample content</p>\n" }
    }


Тест-кейс 5. Изменение комментария: 

Предусловия: Существует комментарий с "id" = {comm_id} с "author_name" != "Anonymous", в запросе использован Basic Auth c корректным логином и паролем

Шаги:

Выполнить PATCH запрос: http://localhost:8000/index.php?rest_route=/wp/v2/comments/{comm_id} с телом запроса: JSON

    {
       "author_name": "Anonymous"
    }

Ожидаемый результат: HTTP 200, JSON 

    {
       "id": {comm_id}
       "author_name": "Anonymous"
    }


Тест-кейс 6. Удаление комментария: 

Предусловия: Существует комментарий с "id" = {comm_id}, в запросе использован Basic Auth c корректным логином и паролем

Шаги:

Выполнить DELETE запрос: http://localhost:8000/index.php?rest_route=/wp/v2/comments/{comm_id}&force=true

Ожидаемый результат: HTTP 200, JSON

    {
      "deleted": true,
      "previous": { "id": {comm_id} }
    }

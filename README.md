# Инструкция
В директории src/test/resources создать файл secrets.properties со следующими параметрами:
* api.username - логин для авторизации на WordPress
* api.password - пароль для авторизации на WordPress
* db.user - пользователь БД wordpress
* db.password - пароль БД wordpress



# Тест-сьют REST API Wordpress http://localhost:8000/

## 1. Тест-кейсы REST API index.php?rest_route=/wp/v2/posts

### 1.1. Эндпоинт POST

### 1.1.1. Создание поста:

Предусловия: В запросе использован Basic Auth c корректным логином и паролем

Шаги:

Выполнить POST запрос:
http://localhost:8000/index.php?rest_route=/wp/v2/posts с телом запроса: JSON

    {
        "status": "publish",
        "title": "New post",
        "content": "sample text"
    }

Ожидаемый результат: HTTP 201, JSON

    {
        "id": целое число,
        "status": "publish",
        "title": { "rendered": "New post" },
        "content": { "rendered": "<p>sample text</p>\n" }
    }

### 1.1.2. Получения созданного поста через запрос к БД:

Предусловия: Создан пост: JSON

    {
        "id" = {post_id},
        "status": "publish",
        "title": "New post",
        "content": "sample text"
    }

, произведено соединение с mysql БД wordpress

Шаги:

Выполнить SQL запрос к БД:

    SELECT post_status, post_title, post_content
    FROM wordpress.wp_posts
    WHERE id =  {post_id}

Ожидаемый результат: Результат SQL запроса:

    post_status = “publish”
    post_title = “New post”
    post_content = "sample text"

### 1.1.3. Получение созданного поста через GET запрос:

Предусловия: Создан пост: JSON

    {
        "id" = {post_id},
        "status": "publish",
        "title": "New post",
        "content": "sample text"
    }

Шаги:

Выполнить GET запрос:
http://localhost:8000/index.php?rest_route=/wp/v2/posts/{post_id}

Ожидаемый результат: GET запрос: HTTP 200, JSON

    {
        "status": "publish",
        "title": { "rendered": "New post" },
        "content": { "rendered": "<p>sample text</p>\n" }
    }

### 1.2. Эндпоинт PATCH

### 1.2.1. Изменение поста:

Предусловия: Создан пост с "id" = {post_id} с "title" != "New new post", в запросе использован Basic Auth c корректным логином и паролем

Шаги:

Выполнить PATCH запрос:
http://localhost:8000/index.php?rest_route=/wp/v2/posts/{post_id} с телом запроса: JSON

    {
        "title": "New new post"
    }

Ожидаемый результат: HTTP 200, JSON 

    {
        "id": {post_id}
        "title": { "rendered": "New new post" }
    }

### 1.2.2. Получения изменённого поста через запрос к БД:

Предусловия: Создан пост: JSON

    {
        "id" = {post_id},
        "status": "publish",
        "title": "New post",
        "content": "sample text"
    }

, произведено соединение с mysql БД wordpress, в запросе PATCH использован Basic Auth c корректным логином и паролем

Шаги:

1) Выполнить PATCH запрос:
   http://localhost:8000/index.php?rest_route=/wp/v2/posts/{post_id} с телом запроса: JSON


       {
           "title": "New new post"
       }


2) Выполнить SQL запрос к БД:


       SELECT post_status, post_title, post_content
       FROM wordpress.wp_posts
       WHERE id =  {post_id}


Ожидаемый результат: Результат SQL запроса:

    post_status = “publish”
    post_title = “New new post”
    post_content = "sample text"

### 1.2.3. Получение изменённого поста через GET запрос:

Предусловия: Создан пост: JSON 

    {
        "id" = {post_id},
        "status": "publish",
        "title": "New post",
        "content": "sample text"
    }

, в запросе PATCH использован Basic Auth c корректным логином и паролем

Шаги:

1) Выполнить PATCH запрос:
   http://localhost:8000/index.php?rest_route=/wp/v2/posts/{post_id} с телом запроса: JSON


       {
       "title": "New new post"
       }


2) Выполнить GET запрос:
   http://localhost:8000/index.php?rest_route=/wp/v2/posts/{post_id}

Ожидаемый результат: GET запрос: HTTP 200, JSON

    {
        "status": "publish",
        "title": { "rendered": "New new post" },
        "content": { "rendered": "<p>sample text</p>\n" }
    }

### 1.3. Эндпоинт DELETE

### 1.3.1. Удаление поста:

Предусловия: Создан пост "id" = {post_id}, в запросе использован Basic Auth c корректным логином и паролем

Шаги:

Выполнить DELETE запрос: http://localhost:8000/index.php?rest_route=/wp/v2/posts/{post_id}&force=true

Ожидаемый результат: HTTP 200, JSON

    {
        "deleted": true,
        "previous": { "id": {post_id} }
    }

### 1.3.2. Получения удалённого поста через запрос к БД:

Предусловия: Создан пост с "id" = {post_id}, произведено соединение с mysql БД wordpress, в запросе DELETE использован Basic Auth c корректным логином и паролем

Шаги:

1) Выполнить DELETE запрос:
   http://localhost:8000/index.php?rest_route=/wp/v2/posts/{post_id}&force=true

2) Выполнить SQL запрос к БД:


       SELECT post_status, post_title, post_content
       FROM wordpress.wp_posts
       WHERE id =  {post_id}


Ожидаемый результат:

SQL запрос вернул пустой набор результатов

### 1.3.3. Получение удалённого поста через GET запрос:

Предусловия: Создан пост с "id" = {post_id}, в запросе DELETE использован Basic Auth c корректным логином и паролем

Шаги:

1) Выполнить DELETE запрос: http://localhost:8000/index.php?rest_route=/wp/v2/posts/{id}&force=true

2) Выполнить GET запрос:
   http://localhost:8000/index.php?rest_route=/wp/v2/posts/{post_id}

Ожидаемый результат: GET запрос: HTTP 404


## 2. Тест-кейсы REST API index.php?rest_route=/wp/v2/comments

### 2.1. Эндпоинт POST

### 2.1.1. Создание комментария:

Предусловия: Создан пост "id" = {post_id}, в запросе использован Basic Auth c корректным логином и паролем

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

### 2.1.2. Получения созданного комментария через запрос к БД:

Предусловия: Создан пост с "id" = {post_id}, cоздан комментарий: JSON 

    {
        “id”: {comm_id},
        "post": {post_id},
        "author_name": "Anon",
        "author_email": "unowen@unknown.com",
        "content": "sample content"
    }

, произведено соединение с БД mysql

Шаги:

Выполнить SQL запрос к БД:

    SELECT comment_post_id, comment_author, comment_author_email, comment_content
    FROM wordpress.wp_comments
    WHERE comment_id =  {comm_id}

Ожидаемый результат: результат SQL запроса:

    comment_post_id = {post_id},
    comment_author = “Anon”,
    comment_author_email = “unowen@unknown.com”,
    comment_content = “sample_content”


### 2.1.3. Получение созданного комментария через GET запрос:

Предусловия: Создан пост "id" = {post_id}, cоздан комментарий: JSON

    {
        “id”: {comm_id},
        "post": {post_id},
        "author_name": "Anon",
        "content": "sample content"
    }

, в запросе использован Basic Auth c корректным логином и паролем

Шаги:

Выполнить GET запрос:
http://localhost:8000/index.php?rest_route=/wp/v2/comments/{comm_id}

Ожидаемый результат: GET запрос: HTTP 200, JSON

    {
        "post": {post_id},
        "author_name": "Anon",
        "content": { "rendered": "<p>sample content</p>\n" }
    }

### 2.2. Эндпоинт PATCH

### 2.2.1. Изменение комментария:

Предусловия: Создан комментарий с "id" = {comm_id} с "author_name" != "Anonymous", в запросе использован Basic Auth c корректным логином и паролем

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

### 2.2.2. Получения изменённого комментария через запрос к БД:

Предусловия: Создан пост с "id" = {post_id}, создан комментарий: JSON

    {
        “id”: {comm_id},
        "post": {post_id},
        "author_name": "Anon",
        "author_email": "unowen@unknown.com",
        "content": "sample content"
    }

, произведено соединение с БД mysql, в запросе PATCH использован Basic Auth c корректным логином и паролем

Шаги:

1) Выполнить PATCH запрос: http://localhost:8000/index.php?rest_route=/wp/v2/comments/{comm_id} с телом запроса: JSON


       {
           "author_name": "Anonymous"
       }


2) Выполнить SQL запрос к БД:


       SELECT comment_post_id, comment_author, comment_author_email, comment_content
       FROM wordpress.wp_comments
       WHERE comment_id =  {comm_id}


Ожидаемый результат: результат SQL запроса:

    comment_post_id = {post_id},
    comment_author = "Anonymous"
    comment_author_email = “unowen@unknown.com”,
    comment_content = “sample_content”

### 2.2.3. Получение изменённого комментария через GET запрос:

Предусловия: Создан пост "id" = {post_id}, cоздан комментарий: JSON

    {
        “id”: {comm_id},
        "post": {post_id},
        "author_name": "Anon",
        "content": "sample content"
    }

, в запросах PATCH, GET использован Basic Auth c корректным логином и паролем

Шаги:

1) Выполнить PATCH запрос: http://localhost:8000/index.php?rest_route=/wp/v2/comments/{comm_id} с телом запроса: JSON


       {
           "author_name": "Anonymous"
       }


2) Выполнить GET запрос:
   http://localhost:8000/index.php?rest_route=/wp/v2/comments/{comm_id}

Ожидаемый результат: GET запрос: HTTP 200, JSON

    {
        "post": {post_id},
        "author_name": "Anon",
        "content": { "rendered": "<p>sample content</p>\n" }
    }

### 2.3. Эндпоинт DELETE

### 2.3.1. Удаление комментария:

Предусловия: Создан комментарий с "id" = {comm_id}, в запросе использован Basic Auth c корректным логином и паролем

Шаги:

Выполнить DELETE запрос: http://localhost:8000/index.php?rest_route=/wp/v2/comments/{comm_id}&force=true

Ожидаемый результат: HTTP 200, JSON

    {
        "deleted": true,
        "previous": { "id": {comm_id} }
    }

### 2.3.2. Получения удалённого комментария через запрос к БД:

Предусловия: Создан комментарий с "id" = {comm_id}, произведено соединение с БД mysql, в запросе DELETE использован Basic Auth c корректным логином и паролем

Шаги:

1) Выполнить DELETE запрос: http://localhost:8000/index.php?rest_route=/wp/v2/comments/{comm_id}

2) Выполнить SQL запрос к БД:


       SELECT comment_post_id, comment_author, comment_author_email, comment_content
       FROM wordpress.wp_comments
       WHERE comment_id =  {comm_id}


Ожидаемый результат:

SQL запрос вернул пустой набор результатов

### 2.3.3. Получение удалённого комментария через GET запрос:

Предусловия: Создан комментарий с "id" = {comm_id}, в запросах DELETE, GET использован Basic Auth c корректным логином и паролем

Шаги:

1) Выполнить DELETE запрос: http://localhost:8000/index.php?rest_route=/wp/v2/comments/{comm_id}&force=true

2) Выполнить GET запрос:
   http://localhost:8000/index.php?rest_route=/wp/v2/comments/{comm_id}

Ожидаемый результат: GET запрос: HTTP 404
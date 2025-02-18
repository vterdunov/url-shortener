# URL Shortener

Сервис для создания и управления короткими ссылками.

## Возможности
- Создание коротких ссылок
- Установка срока жизни ссылки
- Установка и изменение лимита переходов
- Отслеживание количества переходов
- Уведомление пользователя о недоступности ссылки

## Начало работы
### Первый вход
1. Запустите программу
1. Выберите "Continue as new user"
1. Сохраните полученный UUID для последующих входов

### Повторный вход
1. Выберите "Login with UUID"
1. Введите ваш UUID

### Основные функции
#### Создание ссылки
1. В главном меню выберите "Create short URL"
1. Введите оригинальный URL
1. Укажите срок жизни в днях (опционально)
1. Задайте лимит переходов (опционально)

#### Открытие ссылки
1. Выберите "Open URL in browser"
1. Введите короткий код ссылки
1. Ссылка откроется в браузере

#### Просмотр ссылок
1. Выберите "List my URLs"
1. Отобразится список ваших ссылок с информацией:
- Короткий URL
- Оригинальный URL
- Срок действия
- Статистика переходов

### Удаление ссылки
1. Выберите "Remove URL"
1. Введите короткий код ссылки

### Изменение лимита переходов
1. Выберите "Update click limit"
1. Введите короткий код ссылки
1. Укажите новый лимит переходов

### Конфигурация
Параметры конфигурации доступны в файле `src/main/resources/config.properties`

Доступен пример конфиграционного файла. Скопируйте его.
```
cp src/main/resources/config.properties.example src/main/resources/config.properties
```

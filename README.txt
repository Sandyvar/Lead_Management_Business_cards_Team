Django Project Setup Guide
==========================

Project Location
----------------
/Users/sandy/Documents/django_project

This is a Django project configured to use a local MySQL database.


What Was Created
----------------
The following Django project files were created:

- manage.py
- config/
  - __init__.py
  - settings.py
  - urls.py
  - asgi.py
  - wsgi.py
- .env
- requirements.txt
- README.txt


Python Virtual Environment
--------------------------
The project uses the existing virtual environment:

venv/

To activate it:

source venv/bin/activate

To check Python:

python --version

Current Python version used:

Python 3.13.2


Installed Packages
------------------
The following packages were installed:

- Django 4.1.13
- PyMySQL 1.2.0
- python-dotenv 1.2.3

They are listed in requirements.txt:

Django==4.1.13
PyMySQL==1.2.0
python-dotenv==1.2.3

To install them again if needed:

pip install -r requirements.txt


Why Django 4.1.13 Was Used
--------------------------
The local MySQL server version is:

MySQL 5.7.24

The latest Django version that was first installed required a newer MySQL version.
Django 6.1 requires MySQL 8.4 or later, so the project was changed to Django 4.1.13
to support the local MySQL 5.7 database.


MySQL Information
-----------------
Local MySQL is running from the Anaconda installation:

/opt/anaconda3/bin/mysql

The MySQL server uses:

Host: 127.0.0.1
Port: 3306
Username: root
Password: root


Databases
---------
The old existing database was:

aarambh_db

This database was not deleted, renamed, or replaced.

A new database was created for this Django project:

django_project_db

The Django project is configured to use:

MYSQL_DATABASE=django_project_db


Environment File
----------------
Database settings are stored in:

.env

Current .env values:

DJANGO_SECRET_KEY=django-insecure-+r0hror98mv6o+jxf)scm$u-)m(s@bo9_q4on+b817png0+vu!
DJANGO_DEBUG=True
DJANGO_ALLOWED_HOSTS=localhost,127.0.0.1

MYSQL_DATABASE=django_project_db
MYSQL_USER=root
MYSQL_PASSWORD=root
MYSQL_HOST=127.0.0.1
MYSQL_PORT=3306


Django MySQL Configuration
--------------------------
In config/settings.py, Django is configured to use the MySQL backend:

DATABASES = {
    "default": {
        "ENGINE": "django.db.backends.mysql",
        "NAME": os.getenv("MYSQL_DATABASE", "django_project_db"),
        "USER": os.getenv("MYSQL_USER", "root"),
        "PASSWORD": os.getenv("MYSQL_PASSWORD", "root"),
        "HOST": os.getenv("MYSQL_HOST", "127.0.0.1"),
        "PORT": os.getenv("MYSQL_PORT", "3306"),
        "OPTIONS": {
            "charset": "utf8mb4",
            "init_command": "SET sql_mode='STRICT_TRANS_TABLES'",
        },
    }
}


PyMySQL Adapter
---------------
The package mysqlclient could not be installed because the system was missing
the native build dependency pkg-config.

Instead, PyMySQL was installed because it is a pure-Python MySQL driver.

In config/__init__.py, PyMySQL is registered as the MySQLdb adapter:

import pymysql

pymysql.install_as_MySQLdb()


How to Start MySQL
------------------
To start the local MySQL server:

mysql.server start

To check MySQL status:

mysql.server status

To connect manually:

mysql -uroot -proot -h127.0.0.1

To show all databases:

mysql -uroot -proot -h127.0.0.1 -e "SHOW DATABASES;"


How the New Database Was Created
--------------------------------
The new database was created with:

mysql -uroot -proot -h127.0.0.1 -e "CREATE DATABASE IF NOT EXISTS django_project_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"


How to Run Django Migrations
----------------------------
Activate the virtual environment first:

source venv/bin/activate

Then run:

python manage.py migrate

This creates Django's required tables in the configured MySQL database.

The following built-in Django apps were migrated:

- admin
- auth
- contenttypes
- sessions


How to Check the Django Project
-------------------------------
Run:

python manage.py check

Expected result:

System check identified no issues (0 silenced).


How to Run the Django Server
----------------------------
Activate the virtual environment:

source venv/bin/activate

Start the server:

python manage.py runserver 127.0.0.1:8000

Open the project in the browser:

http://127.0.0.1:8000/


How to Verify Django Is Connected to the Correct Database
---------------------------------------------------------
Run:

python manage.py shell -c 'from django.db import connection; cursor = connection.cursor(); cursor.execute("SELECT DATABASE()"); print(cursor.fetchone()[0])'

Expected output:

django_project_db


Important Notes
---------------
- aarambh_db still exists and was not removed.
- django_project_db is the database used by this Django project.
- The Django development server was verified at http://127.0.0.1:8000/.
- A test request returned HTTP 200 OK.
- This folder was not a Git repository at the time of setup.


Common Commands
---------------
Activate virtual environment:

source venv/bin/activate

Install dependencies:

pip install -r requirements.txt

Start MySQL:

mysql.server start

Run migrations:

python manage.py migrate

Start Django:

python manage.py runserver 127.0.0.1:8000

Check current database:

python manage.py shell -c 'from django.db import connection; cursor = connection.cursor(); cursor.execute("SELECT DATABASE()"); print(cursor.fetchone()[0])'

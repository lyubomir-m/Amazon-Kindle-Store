<h1 align="center" id="amazon-kindle-store"><img src="screenshots/Amazon_Kindle_logo.svg" alt="icon"  height="100" tile="Amazon Kindle Store"/></h1>

![Screenshot 1](screenshots/screenshot1.png)

## Overview

**Built with**
<div style="display: flex; align-items: flex-start;">
   <img src="https://techstack-generator.vercel.app/java-icon.svg" alt="icon" width="100" height="100" tile="Java"/>
   <img width="100" src="https://github.com/lyubomir-m/lyubomir-m/blob/48c42d2e0d6616ca8e58f10137d16c9b4f2af2bb/icons/spring.webp" alt="Spring" title="Spring"/>
   <img width="100" src="https://user-images.githubusercontent.com/25181517/183891303-41f257f8-6b3d-487c-aa56-c497b880d0fb.png" alt="Spring Boot" title="Spring Boot"/>
   <img src="https://techstack-generator.vercel.app/mysql-icon.svg" alt="icon" width="100" height="100" tile="MySQL"/>
   <img width="100" height="100" src="https://github.com/lyubomir-m/lyubomir-m/blob/48c42d2e0d6616ca8e58f10137d16c9b4f2af2bb/icons/intellij.png" alt="IntelliJ" title="IntelliJ IDEA"/>
   <img width="100" src="https://user-images.githubusercontent.com/25181517/192108372-f71d70ac-7ae6-4c0d-8395-51d8870c2ef0.png" alt="Git" title="Git"/><br>
   <img src="https://techstack-generator.vercel.app/github-icon.svg" alt="icon" width="100" height="100" tile="GitHub"/>
   <img src="https://techstack-generator.vercel.app/js-icon.svg" alt="icon" width="100" height="100" tile="JavaScript"/>
   <img width="100" src="https://user-images.githubusercontent.com/25181517/192158954-f88b5814-d510-4564-b285-dff7d6400dad.png" alt="HTML" title="HTML"/>
   <img height="100" width="100" src="https://cdn.simpleicons.org/thymeleaf" tile="Thymeleaf"/>
   <img width="100" src="https://user-images.githubusercontent.com/25181517/183898674-75a4a1b1-f960-4ea9-abcb-637170a00a75.png" alt="CSS" title="CSS"/>
   <img width="100" src="https://user-images.githubusercontent.com/25181517/183898054-b3d693d4-dafb-4808-a509-bab54cf5de34.png" alt="Bootstrap" title="Bootstrap"/>
</div>

This is a clone of the [Amazon Kindle Store](https://www.amazon.com/Kindle-Store/b?ie=UTF8&node=133140011).
The store supports a hierarchy of eBook categories (N-ary tree), multiple currencies, pagination, filtering, sorting, users, roles,
authors, publishers, wishlists, shopping carts and orders. Customers can add, delete, and update ratings and reviews and
also search for books by title, author(s) name(s) and publisher name. Project Statistics: 193000 eBooks, 5909
categories, 13200 authors, 152 publishers, and 8 currencies.

The web application is designed to be responsive, ensuring an optimal viewing experience across various devices including monitors, laptops, tablets, and phones.
Some of the other application features are:
- Daily scheduled tasks to get the latest exchange rates via an API, update book prices in foreign currency, and back up the database
- Use of AJAX requests, modals, and DOM manipulation in order to improve the user experience by enabling the following functionalities without a full-page reload:
  - Purchasing books
  - Adding books to the user's list or shopping cart and updating the number of books in the cart
  - Submitting ratings and reviews
  - Dynamically updating the book's average rating and count of ratings upon submission
  - Deleting the last review and displaying the new review on top
  - Deleting books from the user’s list or cart, deleting ratings or reviews and showing the items as deleted
- Cookie for saving the selected currency of users who are not logged in
- Performance logs, login successes and failures logs
- One-day caching for: the currencies in the currency dropdown, the categories in the categories dropdown, the homepage books, and the homepage BookDtos for each currency
- Use of recursive common table expressions in order to find all parent categories and all subcategories of a given category

### Screenshots

![ER Diagram](screenshots/er_diagram.png)

**The statistics below do not include tests**

![Screenshot 10](screenshots/statistics.png)
<p align="center">
  <img src="screenshots/screenshot2.png" alt="Screenshot 2" width="400"/> <img src="screenshots/screenshot3.png" alt="Screenshot 3" width="400"/>
  <img src="screenshots/screenshot4.png" alt="Screenshot 4" width="400"/> <img src="screenshots/screenshot5.png" alt="Screenshot 5" width="400"/>
  <img src="screenshots/screenshot6.png" alt="Screenshot 6" width="400"/> <img src="screenshots/screenshot7.png" alt="Screenshot 7" width="400"/>
  <img src="screenshots/screenshot8.png" alt="Screenshot 8" width="400"/> <img src="screenshots/screenshot9.png" alt="Screenshot 9" width="400"/>
</p>

## Video Presentation

You can find below parts one and two of a video presentation of my clone project uploaded on YouTube.
<p align="center">
  <a href="https://youtu.be/IuM2iNIxQKQ">
    <img src="https://img.youtube.com/vi/IuM2iNIxQKQ/maxresdefault.jpg" alt="eBook Store part 1" width="400"/>
  </a>
  <a href="https://youtu.be/onnj3m1jVr0">
    <img src="https://img.youtube.com/vi/onnj3m1jVr0/maxresdefault.jpg" alt="eBook Store part 2" width="400"/>
  </a>
</p>

## Installation

1. Clone or download this repository
2. Configure the following environment variables related to the database that you would like to use and the exchange
   rate API key:

```properties
# Database Properties
spring.datasource.driverClassName=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/ebook_store?useSSL=false&createDatabaseIfNotExist=true&allowPublicKeyRetrieval=true
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

# Database Backup Properties
backup.database.name=ebook_store
backup.database.user=${DB_USERNAME}
backup.database.password=${DB_PASSWORD}
backup.file.path=src/database_backups
backup.dump.path=/usr/local/mysql/bin/mysqldump

# Free API key for getting the latest exchange rates with 1500 monthly requests
# You can use this key or get your own key by registering at https://www.exchangerate-api.com/
exchangerate.api.key=a9efd8f3e3c147edd16e527e
```

3. Run the application

Whenever the application is run, the InitialDatabaseSetup component checks whether the "publishers" table is empty and, if it is, generates the database. The TaskStartupRunner component checks if
the database has been backed up and if the foreign exchange rates have been updated today. If not, it backs up the
database, updates the exchange rates, and then updates the book prices in foreign currency. The database generation,
backup, and updating the exchange rates and book prices takes about 6 minutes on my laptop.

> [!NOTE]
> The original book data used in this project is sourced from
> the [Kyushu University / book-dataset](https://github.com/uchidalab/book-dataset/tree/master/Task2) public repository.
> The CSV file included in my repository has been modified for use in this specific application. I have used the book
> titles, cover image urls, and categories from the original dataset. These categories have been filtered and mapped
> to the categories that can be seen on the official Kindle Store. The InitialDatabaseSetup has generated the rest 
> of the data - author names, publisher names, count of ratings, average rating, count of purchases, publication 
> dates, subcategory names, placeholder reviews, etc.

## Usage

You need to enter an email and a password in order to log in to the application. The InitialDatabaseSetup component
generates the following two accounts

| Email          | Password |
|----------------|----------|
| admin@mail.com | 1234     |
| user@mail.com  | 1234     |

admin@mail.com has the roles "ADMIN, USER", and user@mail.com has the role "USER". Only accounts with the ADMIN role can
access the admin panel, which allows them to add and remove the ADMIN role from other accounts.

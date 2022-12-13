## ProfitSoft Task 5-6

2. Розробити програму, яка на вхід отримує xml-файл з тегами <person>, в яких є атрибути name і surname.
   Програма повинна створювати копію цього файлу, в якій значення атрибута surname об'єднане з name.
   Наприклад name="Тарас" surname="Шевченко" у вхідному файлі повинно бути замінене на name="Тарас Шевченко" (атрибут surname має бути видалений).
   Вхідний файл може бути великий, тому завантажувати його цілком в оперативну пам'ять буде поганою ідеєю.
* Опціонально (на макс. бал): зробити так, щоб форматування вихідного файла повторювало форматування вхідного файлу (мабуть, xml-парсер в такому разі тут не підійде).


Приклад вхідного файлу:
<persons>
<person name="Іван" surname="Котляревський" birthDate="09.09.1769" />
<person surname="Шевченко" name="Тарас" birthDate="09.03.1814" />
<person
birthData="27.08.1856"
name = "Іван"
surname = "Франко" />
<person name="Леся"
surname="Українка"
birthData="13.02.1871" />
</persons>

Приклад вихідного файлу:
<persons>
<person name="Іван Котляревський" birthDate="09.09.1769"  />
<person name="Тарас Шевченко" birthDate="09.03.1814" />
<person
birthData="27.08.1856"
name = "Іван Франко"
/>
<person name="Леся Українка"

birthData="13.02.1871" />
</persons>


2. У папці є перелік текстових файлів, кожен із яких є "зліпок" БД порушень правил дорожнього руху протягом певного року.
   Кожен файл містить список json (або xml - на вибір) об'єктів - порушень приблизно такого виду:
   {
   "date_time: "2020-05-05 15:39:03", // час порушеня
   "first_name": "Ivan",
   "last_name": "Ivanov"
   "type": "SPEEDING", // тип порушення
   "fine_amount": 340.00 // сума штрафу
   }

Прочитати дані з усіх файлів, розрахувати та вивести статистику порушень у файл. В вихідному файлі повинні міститися загальні суми штрафів за кожним типом порушень за всі роки, відсортовані за сумою (спочатку за найбільшою сумою штрафу).
Якщо вхідний файл був json, то вихідний повиннен бути xml. Якщо вхідний xml, то вихідний - json. Щоб ви мали досвід роботи з обома форматами.
* Опціонально (на макс. бал): зробити так, щоб вхідні файли не завантажувалися цілком в пам'ять.

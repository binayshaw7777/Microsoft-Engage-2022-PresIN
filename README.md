![Banner_final](https://user-images.githubusercontent.com/62587060/170310091-19c3c843-f859-42fb-898f-1b3ea5b7005d.png)

# PresIN - Face-Recognition_Engage-2022
This is an Android Application developed for Microsoft Intern Engage 2022 Programme.<br />
It's an Smart Attendance App that is powered by face recognition technology.<br />
The UI/UX is really simple and self explanatory, the user will not face any issues.<br />
<br />

## 💻 Tech Stack used 
- **Java** - Developed using Android studio and Java as developing Language.<br />
- **Figma** - Designed using Figma (https://www.figma.com/file/jjNrOhXOxz2nnSpAYamNsA/PresIN-Engage?node-id=107%3A21).<br />
- **XML** - To implement design into code.<br />
- **Firebase** - To authenticate users, store user's data and perform operations and store files (PDFs).<br />
- **ML Kit** - To detect faces in the application.<br />
- **TensorFlow Lite** - To recognize Faces.<br />

## ✨ Features
- Face Recognition - Makes it an MVP app
- Attendees list - Shows a list of attendees that are present on that particular day
- Progress - To check the monthly attendance progress and consistency
- PDF - Admin can add PDFs and anyone can download it, comes in handy when sharing important pdfs, previours year papers and so on
- Study timer - This needs the DND Access, it's a timer that monitors how long you study and turns on silent mode when started
- Study progress - This shows how long you studied each day for the past 7 days and this helps your to check your progress
- Settings/Profile/Edit profile - Check your current details and also get the access to edit your profile.

## 🤔 How it works?
- Intialially during registration the user's face embeddings is stored in a HashMap as value with a key that is same of all users "added".<br />
- Then after signin in, the key "added" is replaced with the userID -> <UserID, Embeddings> and stored in the user's Node<br />
- These node from users is then used during facial recognition<br />
- During facial recognition, the embeddings obtained from the person in the camera is used to calculate the euclidean distance between the person and the list of embeddings in the Firebase   RealTime Database.<br />
- If the distance is less than 1.0f and also the Key -> userID of the same hashmap is matched with the current logged in userID then it is success, the attendanc eis marked.<br />
- For reference, follow the image below - https://drive.google.com/file/d/11CJ6chlEreQHYmbqoiFqCBhYz6IUJANm/view?usp=sharing

## Flow of the Application
![PresIN](https://user-images.githubusercontent.com/62587060/170339689-8665f94f-f158-4717-80b1-8713251e6bc8.png)



## Developed by
- [Binay Shaw](https://www.github.com/binayshaw7777)

## 🔗 Links
[![Behance](https://img.shields.io/badge/Behance-1769ff?style=for-the-badge&logo=behance&logoColor=white)](https://katherinempeterson.com/)
[![linkedin](https://img.shields.io/badge/linkedin-0A66C2?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/binayshaw7777/)
[![twitter](https://img.shields.io/badge/twitter-1DA1F2?style=for-the-badge&logo=twitter&logoColor=white)](https://twitter.com/binayplays7777)
[![LeetCode](https://img.shields.io/badge/LeetCode-000000?style=for-the-badge&logo=LeetCode&logoColor=#d16c06)](https://leetcode.com/binayshaw7777/)
[![Instagram](https://img.shields.io/badge/im_yonderly-%23E4405F.svg?style=for-the-badge&logo=Instagram&logoColor=white)](https://www.instagram.com/im_yonderly/)

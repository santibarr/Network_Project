# Group 18 Playbook
# **🧍Names:**
    - Andrea Sanchez: andrea.sanchez@ufl.edu
    - Pedro Camargo: pedro.camargo@ufl.edu
    - Santiago Barrios: santiago.barrios@ufl.edu
# **Roles:**
    - Andrea Sanchez: Worked on Peer, P2Plog, PeerConnection, Choke, Handshake, project debugging, and ReadMe file.
    - Pedro Camargo: Worked on Peer, Choke, Handshake, PeerProcess, project debugging, and project Video. 
    - Santiago Barrios: Worked on OptUnchoke, Message, Destroy, P2Plog, PeerConnection, and ReadMe file
# **🔗Youtube Link:**
https://youtu.be/4RLVXa6SQSw
# **🗣️ Project Overview:**
This project is a P2P File Sharing Protocol. 
The protocol is driven by messages of the following types:
- Handshake
- Choke
- Unchoke
- Interested
- Not Interested
- Have
- Bitfield
- Request
- Piece

The goal is to distribute and manage a file across a network of six peers, utilizing this protocol and its mechanisms to efficiently handle file sharing among the peers.
# **📄Project Description:**
This is the file that outlines the description and instructions for this project

https://drive.google.com/file/d/1FMoCrQCoMLDbp1vucpZ-fcopopzY0cOJ/view?usp=sharing
# **⚙️ Setup Steps:**
The whole project is located in the src folder. We were only able to run the code in localhost since we were not able to connect to the CISE machines.

1. Make sure to change the path of the Common.cfg file in BufferReaderCommonCfg.java line 52 and the PeerInfo.cfg file in BufferReaderPeerInfo in line 15 to the path where these files are in your directory.
2. Open 6 terminals on your computer.
3. Navigate to the directory in all of the terminals
4. Run the following command to compile all java classes:
   ```
   javac *.java
   ```
5. Each terminal is going to run a different peer, based on a PeerId passed in as Command Line Argument.
6. On each terminal, run java PeerProcess <peerID>. Check PeerInfo.cfg for valid PeerID's.
   ```
   //each line is for a different terminal
   java PeerProcess 1001
   java PeerProcess 1002
   java PeerProcess 1003
   java PeerProcess 1004
   java PeerProcess 1005
   java PeerProcess 1006
   ```
7. The program will start running and you will see the logs of the program.

# **What we were able to achieve and what we did not:** 

*What we were able to do*
- Read in the PeerInfo.cfg and Common.cgf files correctly
- Effectively set up peer folders with the corresponding file that the pieces will be read into
- Create a connection between all the peers
- Send the handshake to the peers
- Send interested message to interested peers
- Send the optimistically unchoking of a peer
- Logged the actions for each peer for the functionalities above

*What needs improvement*

We encountered issues in capturing logs and having the effective logic for the preferred neighbors, and in the functionality of the 'have' message, piece downloading, and the choking and unchoking processes. Although we developed the code for these features, they did not perform as expected. The logic for choking and unchoking is contained within the Choke.java and OptUnchoke.java files. The logic for the 'have' message and download of pieces is also contained in the PeerConnection.java file.
    

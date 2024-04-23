# Group 18 Playbook
# **🧍Names:**
    - Andrea Sanchez: andrea.sanchez@ufl.edu
    - Pedro Camargo: pedro.camargo@ufl.edu
    - Santiago Barrios: santiago.barrios@ufl.edu
# **Roles:**
    - Andrea Sanchez: Worked on Peer, P2Plog, PeerConnection, Choke, Handshake, project debugging, and ReadMe          file.
    - Pedro Camargo: Worked on Peer, Choke, Handshake, PeerProcess, project debugging, and project Video. 
    - Santiago Barrios: Worked on OptUnchoke, Message, Destroy, P2Plog, PeerConnection, and ReadMe file

Each member contributed equally to project and there were no complications

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

# **⚙️ Setup Steps:**
We were only able to run the code in localhost since we were not able to connect to the CISE machines.

1. Open 6 terminals on your computer.
2. Navigate to the directory in all of the terminals
3. Run the following command to compile all java classes: "javac *.java"
4. Each terminal is going to run a different peer, based on a PeerId passed in as Command Line Argument.
5. On each terminal, run java PeerProcess <peerID>. Check PeerInfo.cfg for valid PeerID's.
6. The program will start running and you will see the logs of the program.

# ** What we were able to achieve and what we did not: **

    

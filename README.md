# file-repository-rest-service

This service is responsible for maintaining files in the file repository and supports the following operations:

- Reading a file from the file repository based on given filename.
- Reading a file from the file repository based on a unique key which will be matched with the *.meta files in the repository.
- Receiving and saving a file in the file repository including the creation of a *.meta file for the received file.
- Updating the status in the meta file belonging to a specific file.

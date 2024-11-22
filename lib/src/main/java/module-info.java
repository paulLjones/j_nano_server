module paul_jones.j_nano_server {
    exports paul_jones.j_nano_server;
    exports paul_jones.j_nano_server.requests;
    exports paul_jones.j_nano_server.requests.body;
    exports paul_jones.j_nano_server.requests.body.form_data;
    exports paul_jones.j_nano_server.router;
    exports paul_jones.j_nano_server.responses;

    // For Tests
    opens paul_jones.j_nano_server.requests;
    opens paul_jones.j_nano_server.requests.body;
}

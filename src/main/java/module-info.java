module org.ici.messengercloneclient {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;


    opens org.ici.messengercloneclient to javafx.fxml;
    exports org.ici.messengercloneclient;
}
    package com.ums.controller;

    import com.ums.data.Subject;
    import com.ums.database.DatabaseManager;
    import javafx.collections.FXCollections;
    import javafx.collections.ObservableList;
    import javafx.event.ActionEvent;
    import javafx.fxml.FXML;
    import javafx.fxml.FXMLLoader;
    import javafx.scene.Parent;
    import javafx.scene.Scene;
    import javafx.scene.control.*;
    import javafx.scene.control.cell.PropertyValueFactory;
    import javafx.stage.Stage;
    import javafx.scene.text.Text;
    import java.io.IOException;
    import java.sql.*;
    import java.util.Optional;


    public class SubjectController {
        // FXML UI elements
        @FXML private TableView<Subject> subjectTable;
        @FXML private TableColumn<Subject, String> colCode;
        @FXML private TableColumn<Subject, String> colName;
        @FXML private TextField txtCode, txtName;
        @FXML private Button btnAdd, btnEdit, btnDelete;
        @FXML private TextField searchField;
        @FXML private TableColumn<Subject, String> colDescription;

        private ObservableList<Subject> allSubjects = FXCollections.observableArrayList(); // Full dataset for search
        private final ObservableList<Subject> subjects = FXCollections.observableArrayList(); // Data displayed in table
        private String userRole = "Student"; // Default user role

        // Sets the user role (used to toggle UI components for students vs. admins)
        public void setUserRole(String role) {
            this.userRole = role;
            configureUIForRole();
        }

        @FXML
        public void initialize() {
            // Set up table column bindings
            colCode.setCellValueFactory(new PropertyValueFactory<>("subjectCode"));
            colName.setCellValueFactory(new PropertyValueFactory<>("subjectName"));
            colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
            colDescription.setCellFactory(column -> createWrappedCell()); // custom text wrapping

            subjectTable.setItems(subjects); // bind data

            importSubjectsFromSQL(); // fetch from DB

            // Button and field event handlers
            btnAdd.setOnAction(e -> addSubject());
            btnEdit.setOnAction(e -> editSubject());
            btnDelete.setOnAction(e -> deleteSubject());
            searchField.setOnKeyReleased(e -> handleSearch());
        }

        /** Hide or disable controls if the logged-in user is a Student */
        private void configureUIForRole() {
            if ("Student".equalsIgnoreCase(userRole)) {
                txtCode.setVisible(false);
                txtName.setVisible(false);
                btnAdd.setVisible(false);
                btnEdit.setVisible(false);
                btnDelete.setVisible(false);

                txtCode.setDisable(true);
                txtName.setDisable(true);
                btnAdd.setDisable(true);
                btnEdit.setDisable(true);
                btnDelete.setDisable(true);
            }
        }

        /** Launches popup to add a new subject */
        @FXML
        private void addSubject() {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ums/admin/SubjectPopup/AddSubject.fxml"));
                Parent root = loader.load();

                Stage stage = new Stage();
                stage.setTitle("Add Subject");
                stage.setScene(new Scene(root));
                stage.setResizable(false);
                stage.showAndWait(); // pause for popup

                importSubjectsFromSQL(); // reload table
            } catch (IOException e) {
                e.printStackTrace(System.out);
            }
        }

        /** Opens the edit subject window with selected subject’s data */
        @FXML
        private void editSubject() {
            Subject selectedSubject = subjectTable.getSelectionModel().getSelectedItem();

            if (selectedSubject == null) {
                showAlert(Alert.AlertType.WARNING, "No Selection", "No subject selected.", "Please select a subject to edit.");
                return;
            }

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ums/admin/SubjectPopup/EditSubject.fxml"));
                Parent root = loader.load();

                EditSubject controller = loader.getController();
                controller.setSubjectData(selectedSubject); // send data

                Stage stage = new Stage();
                stage.setTitle("Edit Subject");
                stage.setScene(new Scene(root));
                stage.setResizable(false);
                stage.showAndWait();

                importSubjectsFromSQL(); // refresh
            } catch (IOException e) {
                e.printStackTrace(System.out);
                showAlert(Alert.AlertType.ERROR, "Error", "Could not load Edit Subject window.", "Make sure to select a subject to edit.");
            }
        }

        /** Deletes the selected subject from the database after confirmation */
        @FXML
        private void deleteSubject() {
            Subject selectedSubject = subjectTable.getSelectionModel().getSelectedItem();

            if (selectedSubject == null) {
                showAlert(Alert.AlertType.WARNING, "No Selection", "No subject selected.", "Please select a subject to delete.");
                return;
            }

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Deletion");
            confirm.setHeaderText("Are you sure you want to delete this subject?");
            confirm.setContentText("Subject Code: " + selectedSubject.getSubjectCode());

            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                String deleteStatement = "DELETE FROM subjects WHERE Code = ?";

                try (Connection conn = DatabaseManager.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(deleteStatement)) {

                    stmt.setString(1, selectedSubject.getSubjectCode());
                    int rowsDeleted = stmt.executeUpdate();

                    if (rowsDeleted > 0) {
                        subjects.remove(selectedSubject);
                        subjectTable.refresh();
                        showAlert(Alert.AlertType.INFORMATION, "Deleted", null, "Subject deleted successfully.");
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Error", "Subject not found in database.", "Nothing was deleted.");
                    }

                } catch (SQLException e) {
                    e.printStackTrace(System.out);
                    showAlert(Alert.AlertType.ERROR, "Error", "Database error occurred.", "Please try again.");
                }
            }
        }

        /** Retrieves all subjects from the database and populates the table */
        private void importSubjectsFromSQL() {
            subjects.clear();
            allSubjects.clear();

            String query = "SELECT * FROM subjects";

            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    Subject subject = new Subject();
                    subject.setSubjectCode(rs.getString("Code"));
                    subject.setSubjectName(rs.getString("Name"));
                    subject.setDescription(switch (subject.getSubjectCode()) {
                        case "MATH001" -> "This course offers a thorough study of algebra and serves as an introduction to calculus...";
                        case "ENG101" -> "Rhetorical techniques, critical analysis, and academic writing are the main topics...";
                        case "CS201" -> "Basic data structures utilizing Python and Java...";
                        case "CHEM200" -> "Atomic theory, molecular structure, chemical reactions...";
                        case "BIO300" -> "The structure and operation of cells, genetic processes...";
                        case "ENGG402" -> "The advanced study of engineering fundamentals...";
                        case "HIST101" -> "Students study major world civilizations from antiquity to the present...";
                        case "MUSIC102" -> "The foundations of music theory, notation, harmony...";
                        case "PSYCH100" -> "An introduction to psychological science, including topics such as personality and development.";
                        default -> "No description available.";
                    });
                    subjects.add(subject);
                }

                allSubjects.addAll(subjects);
                subjectTable.setItems(subjects);
            } catch (SQLException e) {
                e.printStackTrace(System.out);
            }
        }

        /** Search handler: Filters the table view based on subject code or name */
        @FXML
        private void handleSearch() {
            String searchTerm = searchField.getText().toLowerCase().trim();

            if (searchTerm.isEmpty()) {
                subjectTable.setItems(FXCollections.observableArrayList(allSubjects));
            } else {
                ObservableList<Subject> filtered = FXCollections.observableArrayList();
                for (Subject s : allSubjects) {
                    if (s.getSubjectCode().toLowerCase().contains(searchTerm) ||
                            s.getSubjectName().toLowerCase().contains(searchTerm)) {
                        filtered.add(s);
                    }
                }
                subjectTable.setItems(filtered);
            }
        }

        /** Custom TableCell that wraps long text and applies tooltip */
        @FXML
        public TableCell<Subject, String> createWrappedCell() {
            return new TableCell<>() {
                private final Text text = new Text();
                private boolean initialized = false;

                {
                    text.getStyleClass().add("wrapped-cell-text");
                    text.setStyle("-fx-padding: 8px;");
                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    setGraphic(text);
                    setStyle("-fx-alignment: TOP-LEFT;");
                }

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    if (!initialized && getTableColumn() != null) {
                        text.wrappingWidthProperty().bind(getTableColumn().widthProperty().subtract(16));
                        initialized = true;
                    }

                    if (empty || item == null) {
                        text.setText("");
                        setTooltip(null);
                    } else {
                        text.setText(item);
                        setTooltip(new Tooltip(item));
                    }
                }
            };
        }

        /** Helper method to check for duplicate subject codes */
        private boolean isDuplicate(String code) {
            return subjects.stream().anyMatch(s -> s.getSubjectCode().equalsIgnoreCase(code));
        }

        /** Displays alert messages in popups */
        private void showAlert(Alert.AlertType type, String title, String header, String content) {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(content);
            alert.showAndWait();
        }
    }


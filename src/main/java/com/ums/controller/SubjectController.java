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
        @FXML private TableView<Subject> subjectTable;
        @FXML private TableColumn<Subject, String> colCode;
        @FXML private TableColumn<Subject, String> colName;
        @FXML private TextField txtCode, txtName;
        @FXML private Button btnAdd, btnEdit, btnDelete;
        @FXML private TextField searchField;
        @FXML private TableColumn<Subject, String> colDescription;

        private ObservableList<Subject> allSubjects = FXCollections.observableArrayList();
        private final ObservableList<Subject> subjects = FXCollections.observableArrayList();
        private String userRole = "Student"; // Default role

        public void setUserRole(String role) {
            this.userRole = role;
            configureUIForRole();
        }

        @FXML
        public void initialize() {
            colCode.setCellValueFactory(new PropertyValueFactory<>("subjectCode"));
            colName.setCellValueFactory(new PropertyValueFactory<>("subjectName"));
            subjectTable.setItems(subjects);
            colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
            colDescription.setCellFactory(column -> createWrappedCell());

            importSubjectsFromSQL();

            btnAdd.setOnAction(e -> addSubject());
            btnEdit.setOnAction(e -> editSubject());
            btnDelete.setOnAction(e -> deleteSubject());
            searchField.setOnKeyReleased(e -> handleSearch());
        }

        /** Disable input fields and buttons if user is a Student */
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

        /** Open popup to add a new subject */
        @FXML
        private void addSubject() {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ums/admin/SubjectPopup/AddSubject.fxml"));
                Parent root = loader.load();

                Stage stage = new Stage();
                stage.setTitle("Add Subject");
                stage.setScene(new Scene(root));
                stage.setResizable(false);
                stage.showAndWait();

                importSubjectsFromSQL();
            } catch (IOException e) {
                e.printStackTrace(System.out);
            }
        }

        /** Edit selected subject in popup */
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
                controller.setSubjectData(selectedSubject);

                Stage stage = new Stage();
                stage.setTitle("Edit Subject");
                stage.setScene(new Scene(root));
                stage.showAndWait();

                importSubjectsFromSQL();
            } catch (IOException e) {
                e.printStackTrace(System.out);
                showAlert(Alert.AlertType.ERROR, "Error", "Could not load Edit Subject window.", "Make sure to select a subject to edit.");
            }
        }

        /** Deletes selected subject after confirmation */
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


        /** Load all subjects from the database */
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
                    switch (subject.getSubjectCode()) {
                        case "MATH001":
                            subject.setDescription("This course offers a thorough study of algebra and serves as an introduction to calculus. Focuses on practical applications in science, math, and engineering as well as analytical thinking. Preparing students for the subsequent calculus courses.");
                            break;
                        case "ENG101":
                            subject.setDescription("Rhetorical techniques, critical analysis, and academic writing are the main topics of this course. In addition to creating essays based on research, students will hone their ability to formulate ideas and support them with reasoning.");
                            break;
                        case "CS201":
                            subject.setDescription("Basic data structures utilizing Python and Java, problem-solving techniques, and fundamental programming ideas are all covered in this course. introduces computational thinking and software development techniques as well.");
                            break;
                        case "CHEM200":
                            subject.setDescription("Atomic theory, molecular structure, chemical reactions, thermodynamics, and lab safety are all thoroughly examined in this course. It gets pupils ready for more coursework in the biological and physical sciences.");
                            break;
                        case "BIO300":
                            subject.setDescription("The structure and operation of cells, genetic processes, and biochemical pathways are all covered in this course. includes laboratory work centred on enzyme activity, DNA analysis, and microscopy. This course will prepare students trying to dive deep into the world of biology.");
                            break;
                        case "ENGG402":
                            subject.setDescription("The advanced study of engineering fundamentals covered in this course includes materials, system integration, dynamics, and statics. Students work in groups on design projects and solve practical problems.");
                            break;
                        case "HIST101":
                            subject.setDescription("Students in this course study the major world civilizations from antiquity to the present, looking at governmental structures, artistic creations, revolutions, and international relations from a historical perspective.");
                            break;
                        case "MUSIC102":
                            subject.setDescription("The foundations of music theory, notation, harmony, and ear training are all covered in this course. In order to cultivate critical listening abilities, students also study the history of music across cultures and eras.");
                            break;
                        case "PSYCH100":
                            subject.setDescription("An introduction to psychological science is given in this course, including topics such as personality, development, cognition, abnormal behaviour, and research methodologies. emphasizes scientific thinking and practical applications.");
                            break;
                        default:
                            subject.setDescription("No description available.");
                    }
                    subjects.add(subject);
                }


                // Clone into allSubjects for reference
                allSubjects.addAll(subjects);

                subjectTable.setItems(subjects);  // Initial full view
            } catch (SQLException e) {
                e.printStackTrace(System.out);
            }


        }


        /** Checks if a subject code already exists */
        private boolean isDuplicate(String code) {
            return subjects.stream().anyMatch(s -> s.getSubjectCode().equalsIgnoreCase(code));
        }

        /** Utility to show alerts */
        private void showAlert(Alert.AlertType type, String title, String header, String content) {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(content);
            alert.showAndWait();
        }

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
                    setStyle("-fx-alignment: TOP-LEFT;"); // align top left
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






    }

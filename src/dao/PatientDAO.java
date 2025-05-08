package dao;

import model.Doctor;
import model.Patient;
import db.JDBCUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PatientDAO implements DAOInterface<Patient>{
    private static String SELECT_Patinent_BY_ID = "SELECT * FROM Patient WHERE id =?;";
    public static PatientDAO getInstance() {
        return new PatientDAO();
    }
    @Override
    public void insert(Patient patient){
        String sql = "INSERT INTO Patients (Name, Phone) VALUES (?, ?)";
        ResultSet generatedKeys = null;
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, patient.getName());
            pstmt.setString(2, patient.getPhone());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    Patient addedPatient = new Patient(generatedId, patient.getName(), patient.getPhone());
                    System.out.println("Thêm bệnh nhân thành công với ID: " + generatedId);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm bệnh nhân: " + e.getMessage());
            e.printStackTrace();
        }
    }


    // Chức năng 2: Hiển thị danh sách tất cả bệnh nhân.
    @Override
    public List<Patient> selectAll() {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT PatientID, Name, Phone FROM Patients";

        try (Connection conn = JDBCUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("PatientID");
                String name = rs.getString("Name");
                String phone = rs.getString("Phone");

                Patient patient = new Patient(id, name, phone);
                patients.add(patient);
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách tất cả bệnh nhân: " + e.getMessage());
            e.printStackTrace();
        }
        return patients;
    }

    //Chức năng 3: Tìm bệnh nhân theo số điện thoại.
    @Override
    public List<Patient> selectByCondition(String phone) {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT PatientID, Name, Phone FROM Patients WHERE Phone = ?";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, phone);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("PatientID");
                    String name = rs.getString("Name");
                    String patientPhone = rs.getString("Phone");

                    Patient patient = new Patient(id, name, patientPhone);
                    patients.add(patient);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm kiếm bệnh nhân theo số điện thoại '" + phone + "': " + e.getMessage());
            e.printStackTrace();
        }
        return patients;
    }
}


package dao;

import db.JDBCUtil;
import model.Doctor;
import model.Patient;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorDAO implements DAOInterface<Doctor> {

    public static DoctorDAO getInstance(){
        return new DoctorDAO();
    }
    private static final String INSERT_Doctor_SQL = "INSERT INTO Doctors (name, specialty) VALUES (?, ?);";
    private static final String SELECT_ALL_DoctorS = "SELECT * FROM Doctors;";
    private static final String SELECT_DoctorS_BY_SPECIALTY = "SELECT * FROM Doctors WHERE specialty = ?;";

    private static final String SELECT_DoctorS_BY_ID = "SELECT * FROM Doctors WHERE id = ?;";
    @Override
    public void insert(Doctor doctor) {
        ResultSet generatedKeys = null;
        try (Connection connection = JDBCUtil.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(INSERT_Doctor_SQL, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, doctor.getName());
            pstmt.setString(2, doctor.getSpecialty());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    Patient addedPatient = new Patient(generatedId, doctor.getName(), doctor.getSpecialty());
                    System.out.println("Thêm bác sĩ thành công với ID: " + generatedId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Doctor> selectAll() {
        List<Doctor> Doctors = new ArrayList<>();

        try (Connection connection = JDBCUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_DoctorS)) {
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("DoctorID");
                String name = rs.getString("name");
                String specialty = rs.getString("specialty");
                Doctors.add(new Doctor(id, name, specialty));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Doctors;
    }

    @Override
    public List<Doctor> selectByCondition(String specialty) {
            List<Doctor> Doctors = new ArrayList<>();

            try (Connection connection = JDBCUtil.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(SELECT_DoctorS_BY_SPECIALTY)) {
                preparedStatement.setString(1, specialty);
                System.out.println(preparedStatement);
                ResultSet rs = preparedStatement.executeQuery();

                while (rs.next()) {
                    int id = rs.getInt("DoctorID");
                    String name = rs.getString("name");
                    Doctors.add(new Doctor(id, name, specialty));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return Doctors;
    }
}

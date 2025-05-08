package appointmentsystem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import db.JDBCUtil;
import model.*;
import dao.*;

public class AppointmentService {
    private static final String SELECT_Patinent_BY_ID = "SELECT * FROM Patients WHERE PatientID =?;";
    private static final String SELECT_DoctorS_BY_ID = "SELECT * FROM Doctors WHERE DoctorID = ?;";

    public Patient selectPatientById(int id){
        Patient patient = null;
        try(Connection connection = JDBCUtil.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_Patinent_BY_ID)) {
            preparedStatement.setInt(1,id);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                String name = rs.getString("name");
                String phone = rs.getString("phone");
                return patient = new Patient(id,name,phone);
            }

        }catch(SQLException e){
            e.printStackTrace();
        }
        return patient;
    }

    public Doctor selectDoctorById(int id){
        Doctor doctor = null;
        try(Connection connection = JDBCUtil.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_DoctorS_BY_ID)) {
            preparedStatement.setInt(1,id);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                String name = rs.getString("name");
                String specialty = rs.getString("specialty");
                return doctor = new Doctor(id,name,specialty);
            }

        }catch(SQLException e){
            e.printStackTrace();
        }
        return doctor;
    }

    private Connection connection;

    public AppointmentService(Connection connection) {
        this.connection = connection;
    }

    // Phương thức đặt lịch hẹn
    public boolean createAppointment(int doctorId, int patientId, String dateTime) {
        try {
            // Kiểm tra xem bác sĩ có lịch hẹn trùng thời gian không
            String checkQuery = "SELECT COUNT(*) FROM Appointments WHERE doctorId = ? AND dateTime = ?";
            try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
                checkStmt.setInt(1, doctorId);
                checkStmt.setString(2, dateTime);

                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        System.out.println("\n[THÔNG BÁO] Bác sĩ đã có lịch hẹn vào thời gian này. Vui lòng chọn thời gian khác!");
                        return false;
                    }
                }
            }

            // Thêm lịch hẹn mới
            String insertQuery = "INSERT INTO Appointments (doctorId, patientId, dateTime) VALUES (?, ?, ?)";
            try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
                insertStmt.setInt(1, doctorId);
                insertStmt.setInt(2, patientId);
                insertStmt.setString(3, dateTime);

                int affectedRows = insertStmt.executeUpdate();

                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = insertStmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int appointmentId = generatedKeys.getInt(1);

                            // Lấy thông tin bác sĩ và bệnh nhân để hiển thị thông báo
                            Doctor doctor = selectDoctorById(doctorId);
                            Patient patient = selectPatientById(patientId);

                            if (doctor != null && patient != null) {
                                System.out.println("\n[THÔNG BÁO] Đặt lịch hẹn thành công!");
                                System.out.println("Mã lịch hẹn: " + appointmentId);
                                System.out.println("Bác sĩ: " + doctor.getName() + " (" + doctor.getSpecialty() + ")");
                                System.out.println("Bệnh nhân: " + patient.getName() + " - SĐT: " + patient.getPhone());
                                System.out.println("Thời gian: " + dateTime);
                                System.out.println("Vui lòng đến đúng giờ. Xin cảm ơn!");
                                return true;
                            }
                        }
                    }
                }
                System.out.println("\n[THÔNG BÁO] Đặt lịch hẹn thất bại. Vui lòng thử lại!");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi đặt lịch hẹn: " + e.getMessage());
            return false;
        }
    }

    // Phương thức hủy lịch hẹn
    public boolean cancelAppointment(int appointmentId) {
        try {
            // Kiểm tra xem lịch hẹn có tồn tại không
            String checkQuery = "SELECT a.doctorId, a.patientId, a.dateTime, d.name as doctorName, " +
                    "d.specialty, p.name as patientName, p.phone " +
                    "FROM Appointments a " +
                    "JOIN Doctors d ON a.doctorId = d.id " +
                    "JOIN Patients p ON a.patientId = p.id " +
                    "WHERE a.appointmentId = ?";

            try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
                checkStmt.setInt(1, appointmentId);

                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        String doctorName = rs.getString("doctorName");
                        String specialty = rs.getString("specialty");
                        String patientName = rs.getString("patientName");
                        String phone = rs.getString("phone");
                        String dateTime = rs.getString("dateTime");

                        // Xóa lịch hẹn
                        String deleteQuery = "DELETE FROM Appointments WHERE appointmentId = ?";
                        try (PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery)) {
                            deleteStmt.setInt(1, appointmentId);

                            int affectedRows = deleteStmt.executeUpdate();

                            if (affectedRows > 0) {
                                System.out.println("\n[THÔNG BÁO] Hủy lịch hẹn thành công!");
                                System.out.println("Mã lịch hẹn: " + appointmentId);
                                System.out.println("Bác sĩ: " + doctorName + " (" + specialty + ")");
                                System.out.println("Bệnh nhân: " + patientName + " - SĐT: " + phone);
                                System.out.println("Thời gian: " + dateTime);
                                System.out.println("Lịch hẹn đã được hủy. Xin cảm ơn!");
                                return true;
                            }
                        }
                    } else {
                        System.out.println("\n[THÔNG BÁO] Không tìm thấy lịch hẹn với mã: " + appointmentId);
                        return false;
                    }
                }
            }

            System.out.println("\n[THÔNG BÁO] Hủy lịch hẹn thất bại. Vui lòng thử lại!");
            return false;
        } catch (SQLException e) {
            System.err.println("Lỗi khi hủy lịch hẹn: " + e.getMessage());
            return false;
        }
    }
}
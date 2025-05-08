package model;
import db.JDBCUtil;

import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class Appointment {
    /*- Tạo class Appointment (appointmentId, doctorId, patientId, dateTime).
    - Viết hàm đặt lịch hẹn (kiểm tra trùng lịch).
    - Viết hàm hủy lịch hẹn.
    - Viết hàm đếm số lịch hẹn theo bác sĩ.
    */
    private int appointmentId;
    private int doctorId;
    private int patientId;
    private LocalDateTime dateTime;

    // Constructor

    public Appointment(int appointmentId, int doctorId, int patientId, LocalDateTime dateTime) {
        this.appointmentId = appointmentId;
        this.doctorId = doctorId;
        this.patientId = patientId;
        this.dateTime = dateTime;
    }

    // Constructor không có ID (cho lịch hẹn mới trước khi được lưu)
    public Appointment(int doctorId, int patientId, LocalDateTime dateTime) {
        this.doctorId = doctorId;
        this.patientId = patientId;
        this.dateTime = dateTime;
    }

    // Getters and Setters
    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return "Cuộc hẹn [ID=" + appointmentId + ", Bác sĩ ID=" + doctorId + ", Bệnh nhân ID=" + patientId
                + ", Thời gian=" + dateTime.format(formatter) + "]";
    }

    /**
     * Đặt lịch hẹn mới
     *
     * @return appointmentId khi thành công, -1 khi thất bại
     */
    public int addAppointment(Appointment appointment) throws SQLException {
        // Kiểm tra trùng lịch
        if (isAppointmentTimeConflicting(appointment.getDoctorId(), appointment.getDateTime())) {
            throw new SQLException("Bác sĩ đã có lịch hẹn khác vào thời điểm này!");
        }

        String sql = "INSERT INTO Appointments (doctorId, patientId, AppointmentDate) VALUES (?, ?, ?)";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, appointment.getDoctorId());
            pstmt.setInt(2, appointment.getPatientId());
            pstmt.setTimestamp(3, Timestamp.valueOf(appointment.getDateTime()));

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedId = generatedKeys.getInt(1);
                        System.out.println("Đã tạo lịch hẹn thành công! ID: " + generatedId);
                        return generatedId;
                    }
                }
            }
            return -1;
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm lịch hẹn: " + e.getMessage());
            throw e;
        }
    }

    // Kiểm tra xem đã có lịch hẹn nào trùng thời gian với bác sĩ này chưa

    private boolean isAppointmentTimeConflicting(int doctorId, LocalDateTime dateTime) throws SQLException {

        // Kiểm tra trong khoảng thời gian 1 giờ(trước 30 phút và sau 30 phút)

        LocalDateTime startTime = dateTime.minusMinutes(30);
        LocalDateTime endTime = dateTime.plusMinutes(30);

        String sql = "SELECT COUNT(*) FROM Appointments WHERE doctorId = ? AND " + " AppointmentDate BETWEEN ? AND ?";

        try (Connection conn = JDBCUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, doctorId);
            pstmt.setTimestamp(2, Timestamp.valueOf(startTime));
            pstmt.setTimestamp(3, Timestamp.valueOf(endTime));

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi kiểm tra trùng lịch: " + e.getMessage());
            throw e;
        }

        return false;
    }

    // Hủy lịch hẹn
    public boolean cancelAppointment(int appointmentId) throws SQLException {
        String sql = "DELETE FROM Appointments WHERE appointmentId = ?";

        try (Connection conn = JDBCUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, appointmentId);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Đã hủy lịch hẹn có ID: " + appointmentId);
                return true;
            } else {
                System.out.println("Không tìm thấy lịch hẹn có ID: " + appointmentId);
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi hủy lịch hẹn: " + e.getMessage());
            throw e;
        }
    }

    // Đếm số lịch hẹn của mỗi bác sĩ

    public static Map<Integer, Integer> countAppointmentsByDoctor() throws SQLException {
        Map<Integer, Integer> countMap = new HashMap<>();
        String sql = "SELECT doctorId, COUNT(*) as appointmentCount FROM Appointments GROUP BY doctorId";

        try (Connection conn = JDBCUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int doctorId = rs.getInt("doctorId");
                int count = rs.getInt("appointmentCount");
                countMap.put(doctorId, count);
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi đếm số lịch hẹn theo bác sĩ: " + e.getMessage());
            throw e;
        }

        return countMap;
    }
}
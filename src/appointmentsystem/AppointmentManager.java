package appointmentsystem;

import java.sql.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Doctor;

public class AppointmentManager {
    private Connection connection;

    public AppointmentManager(Connection connection) {
        this.connection = connection;
    }

    // Hiển thị lịch hẹn theo bác sĩ
    public void displayAppointmentsByDoctor(int doctorId) {
        try {
            // Trước tiên, kiểm tra xem bác sĩ có tồn tại không
            String checkDoctorQuery = "SELECT name, specialty FROM Doctors WHERE id = ?";
            Doctor doctor = null;

            try (PreparedStatement stmt = connection.prepareStatement(checkDoctorQuery)) {
                stmt.setInt(1, doctorId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String name = rs.getString("name");
                        String specialty = rs.getString("specialty");
                        doctor = new Doctor(doctorId, name, specialty);
                    } else {
                        System.out.println("Không tìm thấy bác sĩ với ID: " + doctorId);
                        return;
                    }
                }
            }

            // Lấy danh sách lịch hẹn của bác sĩ
            String query = "SELECT a.appointmentId, a.patientId, p.name as patientName, " +
                    "p.phone as patientPhone, a.dateTime " +
                    "FROM Appointments a " +
                    "JOIN Patients p ON a.patientId = p.id " +
                    "WHERE a.doctorId = ? " +
                    "ORDER BY a.dateTime";

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, doctorId);

                try (ResultSet rs = stmt.executeQuery()) {
                    System.out.println("\n=== LỊCH HẸN CỦA BÁC SĨ: " + doctor.getName() + " (" + doctor.getSpecialty() + ") ===");
                    System.out.println("----------------------------------------------------------------------");
                    System.out.printf("%-5s | %-20s | %-15s | %-20s\n",
                            "ID", "TÊN BỆNH NHÂN", "ĐIỆN THOẠI", "THỜI GIAN");
                    System.out.println("----------------------------------------------------------------------");

                    boolean hasAppointments = false;

                    while (rs.next()) {
                        hasAppointments = true;
                        int appointmentId = rs.getInt("appointmentId");
                        String patientName = rs.getString("patientName");
                        String patientPhone = rs.getString("patientPhone");
                        String dateTime = rs.getString("dateTime");

                        System.out.printf("%-5d | %-20s | %-15s | %-20s\n",
                                appointmentId, patientName, patientPhone, dateTime);
                    }

                    if (!hasAppointments) {
                        System.out.println("Bác sĩ này chưa có lịch hẹn nào.");
                    }

                    System.out.println("----------------------------------------------------------------------");
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi hiển thị lịch hẹn theo bác sĩ: " + e.getMessage());
        }
    }

    // Hiển thị danh sách tất cả các bác sĩ để người dùng chọn
    public void displayDoctorsForSelection() {
        try {
            String query = "SELECT id, name, specialty FROM Doctors ORDER BY name";

            try (PreparedStatement stmt = connection.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {

                System.out.println("\n=== DANH SÁCH BÁC SĨ ===");
                System.out.println("-------------------------------------------");
                System.out.printf("%-5s | %-20s | %-20s\n", "ID", "HỌ TÊN", "CHUYÊN MÔN");
                System.out.println("-------------------------------------------");

                boolean hasDoctors = false;

                while (rs.next()) {
                    hasDoctors = true;
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    String specialty = rs.getString("specialty");

                    System.out.printf("%-5d | %-20s | %-20s\n", id, name, specialty);
                }

                if (!hasDoctors) {
                    System.out.println("Chưa có bác sĩ nào trong hệ thống.");
                }

                System.out.println("-------------------------------------------");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi hiển thị danh sách bác sĩ: " + e.getMessage());
        }
    }
}
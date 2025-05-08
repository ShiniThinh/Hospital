package appointmentsystem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.*;

public class DoctorManager {

    //Giả sử có một kết nối đến database
    private Connection connection;

    public DoctorManager(Connection connection) {
        this.connection = connection;
    }

    // Phương thức lấy danh sách bác sĩ và số lịch hẹn
    public DoctorAppointmentCount[] getDoctorAppointmentCounts() throws SQLException {
        // Truy vấn lấy danh sách bác sĩ và số lịch hẹn
        String query = "SELECT d.DoctorID, d.name, d.specialty, COUNT(a.appointmentId) as appointmentCount " +
                "FROM Doctors d LEFT JOIN Appointments a ON d.DoctorID = a.DoctorID " +
                "GROUP BY d.DoctorID, d.name, d.specialty";

        List<DoctorAppointmentCount> doctorsList = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("DoctorID" );
                String name = rs.getString("name");
                String specialty = rs.getString("specialty");
                int appointmentCount = rs.getInt("appointmentCount");

                Doctor doctor = new Doctor(id, name, specialty);
                doctorsList.add(new DoctorAppointmentCount(doctor, appointmentCount));
            }
        }

        DoctorAppointmentCount[] doctorsArray = doctorsList.toArray(
                new DoctorAppointmentCount[0]);

        return doctorsArray;
    }

    // Phương thức hiển thị danh sách bác sĩ đã sắp xếp theo số lịch hẹn
    public void displayDoctorsSortedByAppointments() {
        try {
            // Lấy danh sách bác sĩ và số lịch hẹn
            DoctorAppointmentCount[] doctors = getDoctorAppointmentCounts();

            // Sắp xếp bác sĩ theo số lịch hẹn (giảm dần) bằng Heap Sort
            HeapSort.heapSort(doctors);

            System.out.println("=== DANH SÁCH BÁC SĨ THEO SỐ LỊCH HẸN (GIẢM DẦN) ===");
            System.out.println("--------------------------------------------------------");
            System.out.printf("%-5s | %-20s | %-20s | %-15s\n", "ID", "HỌ TÊN", "CHUYÊN MÔN", "SỐ LỊCH HẸN");
            System.out.println("--------------------------------------------------------");

            for (DoctorAppointmentCount doc : doctors) {
                Doctor doctor = doc.getDoctor();
                System.out.printf("%-5d | %-20s | %-20s | %-15d\n",
                        doctor.getId(), doctor.getName(), doctor.getSpecialty(), doc.getAppointmentCount());
            }

            System.out.println("--------------------------------------------------------");
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy và hiển thị danh sách bác sĩ: " + e.getMessage());
        }
    }
}

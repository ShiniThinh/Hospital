import appointmentsystem.AppointmentService;
import appointmentsystem.DoctorManager;
import dao.DoctorDAO;
import dao.PatientDAO;
import db.JDBCUtil;
import model.Appointment;
import model.Doctor;
import model.Patient;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Connection connection = JDBCUtil.getConnection();
        Scanner scanner = new Scanner(System.in);
        PatientDAO patientDAO = PatientDAO.getInstance();
        DoctorDAO doctorDAO = DoctorDAO.getInstance();

        while (true) {
            System.out.println("\n===== HỆ THỐNG QUẢN LÝ BỆNH VIỆN =====");
            System.out.println("1. Thêm bệnh nhân");
            System.out.println("2. Xem danh sách bệnh nhân");
            System.out.println("3. Tìm bệnh nhân theo số điện thoại");
            System.out.println("4. Thêm bác sĩ");
            System.out.println("5. Xem danh sách bác sĩ");
            System.out.println("6. Tìm bác sĩ theo chuyên môn");
            System.out.println("7. Đặt lịch hẹn");
            System.out.println("8. Hủy lịch hẹn");
            System.out.println("9. Thống kê số lịch hẹn theo bác sĩ");
            System.out.println("10.Sắp xếp bác sĩ theo số lịch hẹn giảm dần bằng HeapSort");
            System.out.println("0. Thoát");
            System.out.print("Chọn chức năng: ");
            int choice = scanner.nextInt();

            scanner.nextLine();

            try {
                switch (choice) {
                    case 1:
                        System.out.print("Nhập tên bệnh nhân: ");
                        String name = scanner.nextLine();
                        System.out.print("Nhập số điện thoại: ");
                        String phone = scanner.nextLine();
                        patientDAO.insert(new Patient(name, phone));
                        break;

                    case 2:
                        List<Patient> patients = patientDAO.selectAll();
                        patients.forEach(System.out::println);
                        break;

                    case 3:
                        System.out.print("Nhập số điện thoại cần tìm: ");
                        String searchPhone = scanner.nextLine();
                        List<Patient> foundPatients = patientDAO.selectByCondition(searchPhone);
                        foundPatients.forEach(System.out::println);
                        break;

                    case 4:
                        System.out.print("Nhập tên bác sĩ: ");
                        String doctorName = scanner.nextLine();
                        System.out.print("Nhập chuyên môn: ");
                        String specialty = scanner.nextLine();
                        doctorDAO.insert(new Doctor(doctorName, specialty));
                        break;

                    case 5:
                        List<Doctor> doctors = doctorDAO.selectAll();
                        doctors.forEach(System.out::println);
                        break;

                    case 6:
                        System.out.print("Nhập chuyên môn cần tìm: ");
                        String spec = scanner.nextLine();
                        List<Doctor> foundDoctors = doctorDAO.selectByCondition(spec);
                        foundDoctors.forEach(System.out::println);
                        break;

                    case 7:
                        System.out.print("Nhập ID bác sĩ: ");
                        int doctorId = scanner.nextInt();
                        System.out.print("Nhập ID bệnh nhân: ");
                        int patientId = scanner.nextInt();

                        scanner.nextLine();

                        System.out.print("Nhập thời gian hẹn (yyyy-MM-dd HH:mm): ");
                        String dateTimeStr = scanner.nextLine();
                        LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));

                        Appointment appointment = new Appointment(doctorId, patientId, dateTime);
                        appointment.addAppointment(appointment);
                        break;

                    case 8:
                        System.out.print("Nhập ID lịch hẹn cần hủy: ");
                        int apptId = scanner.nextInt();
                        new Appointment(0,0,null).cancelAppointment(apptId);
                        break;

                    case 9:
                        Map<Integer, Integer> appointmentStats = Appointment.countAppointmentsByDoctor();
                        appointmentStats.forEach((id, count) -> System.out.println("Bác sĩ ID: " + id + ", số lịch hẹn: " + count));
                        break;

                    case 10:
                        DoctorManager doctorManager = new DoctorManager(connection);
                        doctorManager.displayDoctorsSortedByAppointments();
                        break;

                    case 0:
                        System.out.println("Thoát chương trình. Tạm biệt!");
                        System.exit(0);

                    default:
                        System.out.println("Chức năng không hợp lệ!");
                }

            } catch (Exception e) {
                System.out.println("Đã xảy ra lỗi: " + e.getMessage());
            }
        }
    }
}

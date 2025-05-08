package appointmentsystem;
import model.*;
public class DoctorAppointmentCount implements Comparable<DoctorAppointmentCount> {
        private Doctor doctor;
        private int appointmentCount;

        public DoctorAppointmentCount(Doctor doctor, int appointmentCount) {
            this.doctor = doctor;
            this.appointmentCount = appointmentCount;
        }

        public Doctor getDoctor() {
            return doctor;
        }

        public int getAppointmentCount() {
            return appointmentCount;
        }

        @Override
        public String toString() {
            return doctor.toString() + " - Số lịch hẹn: " + appointmentCount;
        }

        @Override
        public int compareTo(DoctorAppointmentCount other) {
            // Sắp xếp giảm dần theo số lịch hẹn
            return Integer.compare(other.appointmentCount, this.appointmentCount);
        }
}

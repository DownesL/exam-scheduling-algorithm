package be.odisee.data;

import be.odisee.domain.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class DataReader {

    private final File eFile;
    private final File sFile;
    private HashMap<Integer, Student> students;
    private HashMap<Integer, Exam> exams;
    private List<TimeSlot> timeslots;
    private final String eFileName;
    private final String sFileName;

    public DataReader(String examFileName, String studentsFileName) {
        eFile = new File(examFileName);
        sFile = new File(studentsFileName);
        this.eFileName = eFile.getName();
        this.sFileName = sFile.getName();
        readTimeSlots();
        readExams();
        readStudents();
    }
    public Map<TimeSlot, List<Exam>> getTimeSlots() {
        File tsf= new File("benchmarks/lol.txt");
        Scanner timeslotScanner;
            HashMap<TimeSlot, List<Exam>> timeslots = new HashMap<>();
        try {
            timeslotScanner = new Scanner(tsf);
            while (timeslotScanner.hasNextLine()) {
                String nextLine = timeslotScanner.nextLine();
                Scanner sc = new Scanner(nextLine);
                sc.useDelimiter(":");
                int examId = sc.nextInt();
                int tsId = sc.nextInt();
                if (timeslots.containsKey(new TimeSlot(tsId))) {
                    timeslots.get(new TimeSlot(tsId)).add(exams.get(examId));
                }else {
                    List<Exam> x = new ArrayList<>();
                    x.add(exams.get(examId));
                    timeslots.put(new TimeSlot(tsId), x);
                }
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return timeslots;
    }

    private void readTimeSlots() {
        if (eFileName.endsWith(".crs")) {
            //exams
            //read number of timeslots
            File timeSlotFile = new File("benchmarks/number_of_timeslots.txt");
            Scanner timeslotScanner;
            try {
                timeslotScanner = new Scanner(timeSlotFile);
                while (timeslotScanner.hasNextLine()) {
                    String nextLine = timeslotScanner.nextLine();
                    if (nextLine.startsWith(eFileName)) {
                        //ok goede gevonden
                        Scanner sc = new Scanner(nextLine);
                        sc.useDelimiter(":");
                        sc.next();
                        int numberOfTimeSlots = sc.nextInt();
                        timeslots = new ArrayList<TimeSlot>();
                        for (int i = 0; i < numberOfTimeSlots; i++) {
                            timeslots.add(new TimeSlot(i));
                        }
                        break;
                    }
                }
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

	private void readExams() {
        Scanner scanner;
        try {
            scanner = new Scanner(eFile);
            this.exams = new HashMap<Integer, Exam>();
            while (scanner.hasNextLine()) {
                String nextLine = scanner.nextLine();
                Scanner sc = new Scanner(nextLine);
                sc.useDelimiter(" ");
                int examID = sc.nextInt();
                int aantal = sc.nextInt();
                Exam exam = new Exam(examID, aantal);
                List<Integer> sid = new ArrayList<Integer>();
                exam.setSID(sid);
                this.exams.put(exam.getID(), exam);
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

	private void readStudents() {
        if (sFileName.endsWith(".stu")) {
            //students
            Scanner scanner;
            students = new HashMap<Integer, Student>();
            try {
                scanner = new Scanner(sFile);
                int teller = 0;
                while (scanner.hasNextLine()) {
                    teller++;
                    String nextLine = scanner.nextLine();
                    Scanner sc = new Scanner(nextLine);
                    sc.useDelimiter(" ");
                    Student student = new Student(teller);
                    List<Integer> examIDList = new ArrayList<Integer>();
                    while (sc.hasNext()) {
                        examIDList.add(sc.nextInt());
                    }
                    for (Integer e : examIDList) {
                        Exam exam = exams.get(e);
                        exam.getSID().add(student.getID());
                    }
                    student.setExamIds(examIDList);
                    students.put(teller, student);

                }
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }


    public HashMap<Integer, Student> getStudents() {
        return students;
    }

    public void setStudents(HashMap<Integer, Student> students) {
        this.students = students;
    }

    public HashMap<Integer, Exam> getExams() {
        return exams;
    }

    public void setExams(HashMap<Integer, Exam> exams) {
        this.exams = exams;
    }

    public List<TimeSlot> getTimeslots() {
        return timeslots;
    }

    public void setTimeslots(List<TimeSlot> timeslots) {
        this.timeslots = timeslots;
    }

    public static void main(String... aArgs) {

        DataReader parser = new DataReader("benchmarks/lse-f-91.crs", "benchmarks/lse-f-91.stu");
        HashMap<Integer, Exam> exams = parser.getExams();
        Set<Integer> keys = exams.keySet();
        for (Integer i : keys) {
            Exam exam = exams.get(i);
            System.out.println(exam.getID() + " " + exam.getSID());
        }
        keys = parser.getStudents().keySet();
        for (Integer i : keys) {
            Student student = parser.getStudents().get(i);
            System.out.println(student.getID());
        }
        keys = exams.keySet();
        int totaal = 0;
        for (Integer i : keys) {
            Exam exam = exams.get(i);
            totaal += exam.getSID().size();
        }
        System.out.println(totaal);
    }

}

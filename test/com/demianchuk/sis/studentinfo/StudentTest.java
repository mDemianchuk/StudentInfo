package com.demianchuk.sis.studentinfo;

import com.demianchuk.sis.studentinfo.Student;
import java.util.logging.Handler;
import java.util.logging.Logger;
import junit.framework.*;



public class StudentTest extends TestCase {
    private static final double GRADE_TOLERANCE = 0.05;
    
    public void testCreate(){
        final String firstStudentName = "Jane Doe";
        Student firstStudent = new Student(firstStudentName);
        assertEquals(firstStudentName, firstStudent.getName());
        assertEquals("Jane", firstStudent.getFirstName());
        assertEquals("Doe", firstStudent.getLastName());
        assertEquals("", firstStudent.getMiddleName());
        
        final String secondStudentName = "Blow";
        Student secondStudent = new Student(secondStudentName);
        assertEquals(firstStudentName, firstStudent.getName());
        assertEquals("", secondStudent.getFirstName());
        assertEquals("Blow", secondStudent.getLastName());
        assertEquals("", secondStudent.getMiddleName());
        
        final String thirdStudentName = "Raymond Douglas Davies";
        Student thirdStudent = new Student(thirdStudentName);
        assertEquals(thirdStudentName, thirdStudent.getName());
        assertEquals("Raymond", thirdStudent.getFirstName());
        assertEquals("Davies", thirdStudent.getLastName());
        assertEquals("Douglas", thirdStudent.getMiddleName());
    }
    
    public void testStudentStatus(){
        Student student = new Student("a");
        assertEquals(0, student.getCredits());
        assertFalse(student.isFullTime());
        
        student.addCredits(3);
        assertEquals(3, student.getCredits());
        assertFalse(student.isFullTime());
        
        student.addCredits(4);
        assertEquals(7, student.getCredits());
        assertFalse(student.isFullTime());
        
        student.addCredits(5);
        assertEquals(12, student.getCredits());
        assertTrue(student.isFullTime());
    }
    
    public void testInState(){
        Student student = new Student("A");
        assertFalse(student.isInState());
        student.setState(Student.IN_STATE);
        assertTrue(student.isInState());
        student.setState("MD");
        assertFalse(student.isInState());
    }
    
    public void testCalculateGpa(){
        Student student = new Student("a");
        assertGpa(student, 0.0);
        student.addGrade(Student.Grade.A);
        assertGpa(student, 4.0);
        student.addGrade(Student.Grade.B);
        assertGpa(student, 3.5);
        student.addGrade(Student.Grade.C);
        assertGpa(student, 3.0);
        student.addGrade(Student.Grade.D);
        assertGpa(student, 2.5);
        student.addGrade(Student.Grade.F);
        assertGpa(student, 2.0);
    }
    
    private void assertGpa(Student student, double expectedGpa){
        assertEquals(expectedGpa, student.getGpa(), GRADE_TOLERANCE);
    }
    
    public void testCalculateHonorsStudentGpa(){
        assertGpa(createHonorsStudent(), 0.0);
        assertGpa(createHonorsStudent(Student.Grade.A), 5.0);
        assertGpa(createHonorsStudent(Student.Grade.B), 4.0);
        assertGpa(createHonorsStudent(Student.Grade.C), 3.0);
        assertGpa(createHonorsStudent(Student.Grade.D), 2.0);
        assertGpa(createHonorsStudent(Student.Grade.F), 0.0);
    }
    
    private Student createHonorsStudent(Student.Grade grade){
        Student student = createHonorsStudent();
        student.addGrade(grade);
        return student;
    }
    
    private Student createHonorsStudent(){
        Student student = new Student("a");
        student.setGradingStrategy(new HonorsGradingStrategy());
        return student;
    }
        
    public void testCharges(){
        Student student = new Student("a");
        student.addCharge(500);
        student.addCharge(200);
        student.addCharge(399);
        assertEquals(1099, student.totalCharges());
    }
    
    public void testBadlyFormattedName(){
        Handler handler = new TestHandler();
        Student.logger.addHandler(handler);
        
        String studentName = "a b c d";
        try{
            new Student(studentName);
            fail("expected exception from 4-part name");
        }
        catch(StudentNameFormatException expectedException){
            String message = String.format(Student.TOO_MANY_NAME_PARTS_MSG,
                        studentName, Student.MAX_NAME_PARTS);
            assertEquals(message, expectedException.getMessage());
            assertEquals(message, ((TestHandler)handler).getMessage());
        }        
    }
     
    public void testLoggingHierarchy(){
        Logger logger = Logger.getLogger("com.demianchuk.sis.Studentinfo.Student");
        assertTrue(logger == Logger.getLogger("com.demianchuk.sis.Studentinfo.Student"));
        
        Logger parent = Logger.getLogger("com.demianchuk.sis.Studentinfo");
        assertEquals(parent, logger.getParent());
        assertEquals(Logger.getLogger("com.demianchuk.sis"), parent.getParent());
    }
    
    public void testFlags(){
        Student student = new Student("a");
        student.set(
            Student.Flag.ON_CAMPUS,
            Student.Flag.TAX_EXEMPT,
            Student.Flag.MINOR);
        
        assertTrue(student.isOn(Student.Flag.ON_CAMPUS));
        assertTrue(student.isOn(Student.Flag.TAX_EXEMPT));
        assertTrue(student.isOn(Student.Flag.MINOR));
        
        assertFalse(student.isOff(Student.Flag.ON_CAMPUS));
        assertTrue(student.isOff(Student.Flag.TROUBLEMAKER));
        
        student.unset(Student.Flag.ON_CAMPUS);
        assertTrue(student.isOff(Student.Flag.ON_CAMPUS));
        assertTrue(student.isOn(Student.Flag.TAX_EXEMPT));
        assertTrue(student.isOn(Student.Flag.MINOR));
        
    }
}

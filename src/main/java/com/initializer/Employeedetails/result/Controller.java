package com.initializer.Employeedetails.result;

import com.initializer.Employeedetails.GS.Employee;
import com.initializer.Employeedetails.GS.Relation;
import com.initializer.Employeedetails.GS.Info;
import com.initializer.Employeedetails.Inter.EmployeeRepo;
import com.initializer.Employeedetails.Inter.RelationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@RestController
@RequestMapping("/api")
public class Controller
{
    @Autowired
    public EmployeeRepo repository;
    @Autowired
    public RelationRepo repository1;
    @RequestMapping("/rest/employees/get/{id}")
    @ResponseBody
   public Map<String,Object> getOne(@PathVariable("id") int id)
   {
        Map<String,Object> mp=new TreeMap<>();
        Employee fuser= repository.findById(id);                                                                        //Fetching details of row with entered ID
        String s=fuser.getName();
        mp.put(s,fuser);
        List<Employee> fchild=repository.findAllByPID(id);                                                              //Fetching details of subordinates of id entered
        mp.put("Reporteee's",fchild);
        int  a=fuser.getpID();
        Employee fparent=repository.findById(a);                                                                        //Fetching details of Manager of ID entered
        if(a!=0)
        {
            mp.put("Manager",fparent);
        }
        List <Employee> obj=repository.findAllByPID(a);
        List<Employee> fcolleague= coll(obj,id);                                                                        //Fetching details of colleagues of entered ID
        mp.put("Colleague : ",fcolleague);
        return mp;
   }
    private List<Employee> coll(List<Employee> fcolleague, int id)                                                       //Method to fetch colleagues(this method removes the details of row itself and returns only colleague's info)
    {
        List<Employee> ls=new ArrayList<>();
        for(int i=0;i<fcolleague.size();i++)
        {
            Employee m=fcolleague.get(i);
            if(m.getId()!=id)
            {
                ls.add(m);
            }
        }
        return ls;
    }

    @RequestMapping("/rest/employees/get")
    public Iterable<Employee> findAll()
    {
        return repository.findAll();                                                                                    //Fetches detail of every entry in the table
    }
    @PostMapping(path = "/rest/employees/post",consumes = {"application/json"})
    public ResponseEntity pos(@RequestBody Info getSet)
    {
        if((getSet.getName()==null) || (getSet.getDesi()==null) || (getSet.getpID()==null))  //Fields required for new entry can not be null
        {
            return new ResponseEntity("Any of the required values can not be null. Enter Name,Designation and PID and try again.",HttpStatus.BAD_REQUEST);
        }
        Employee gs=new Employee();
        gs.setName(getSet.getName());                                                                                   //Set name that is entered by user in new entry
        gs.setpID(getSet.getpID());                                                                                     //Set parent id that is entered by user in new entry
        Relation gs1 = repository1.findByDesi(getSet.getDesi());                                                        //Fetch designation from Relation Table
        gs.setDesi(getSet.getDesi());                                                                                   //Set designation that is entered by user in new entry
        gs.setJid(gs1);                                                                                                 //Set Job ID by using designation field of Relation table(automatic adoption of JOB id with designation)
        Employee gss=repository.findById(gs.getpID());
        int a=gs1.getJid();
        int b=gss.getJid().getJid();
        if(a<=b)                                                                                                        //Compare Job ID's of entered record and parent
        {
            return new ResponseEntity("Designation can not be same or higher",HttpStatus.BAD_REQUEST);
        }
        repository.save(gs);
        return new ResponseEntity(gs,HttpStatus.OK);
    }
    @DeleteMapping(value = "/rest/employees/delete/{id}")
    public ResponseEntity deleteOne(@PathVariable("id") int id)
    {
        Employee s=repository.findById(id);                                                                             //Getting row information of id that is required to be deleted
        String st=s.getDesi();                                                                                          //Getting designation of row to be deleted
        if(st.equals("Director"))                                                                                       //Checking if designation is director(director can not be deleted)
        {
            return new ResponseEntity("Director can not be deleted",HttpStatus.BAD_REQUEST);
        }
        int p=s.getpID();                                                                                               //Getting parent id of row to be deleted
        List<Employee> ls=repository.findAllByPID(id);                                                                  //Finding parent id equal to Id of row to be deleted(Children of row to be deleted)
        for(int i=0;i<ls.size();i++)
        {
            Employee lp=ls.get(i);
            lp.setpID(p);                                                                                               //Changing Parent id of each child to parent id of row to be deleted
        }
                        repository.deleteById(id);                                                                      //Deletes row with id entered
        return new ResponseEntity("Record deleted",HttpStatus.OK);
    }
    @PutMapping(value = "/rest/employees/put/{id}")
    public ResponseEntity updateOne(@PathVariable("id") int id,@RequestBody Info user)
    {   Employee up = repository.findById(id);
        if(user.isReplace())
        {   if (up == null) {
            return new ResponseEntity("Unable to update. User with id " + id + " does not exist.", HttpStatus.NOT_FOUND);
        }
            Employee ups=new Employee();
            String str=user.getName();
            if(up.getName().equals(str))
            {
                return new ResponseEntity("Record Already Exists",HttpStatus.BAD_REQUEST);
            }
            ups.setName(user.getName());
            String str1=up.getDesi();
            String str2=user.getDesi();
            if(!str1.equals(str2))
            {
                return new ResponseEntity("Designations of row being replaced have to be same",HttpStatus.BAD_REQUEST);
            }
            else
            {
                ups.setDesi(up.getDesi());
            }
            ups.setpID(up.getpID());
            Relation relation1 = repository1.findByDesi(up.getDesi());
            ups.setJid(relation1);
            repository.save(ups);
            int p=ups.getId();
            List<Employee> ls=repository.findAllByPID(id);
            for(int i=0;i<ls.size();i++)
            {
                Employee lp=ls.get(i);
                lp.setpID(p);
            }
            repository.deleteById(id);
            return new ResponseEntity(ups,HttpStatus.OK);
        }
        else {

            if (up == null) {
                return new ResponseEntity("Unable to update. User with id " + id + " does not exist.", HttpStatus.NOT_FOUND);
            }
            up.setName(user.getName());
            up.setpID(up.getpID());
            up.setDesi(up.getDesi());
            Relation relation = repository1.findByDesi(up.getDesi());
            Employee gss = repository.findById(up.getpID());
            up.setJid(relation);
//            int a = relation.getJid();
//            int b = gss.getJid().getJid();
//            if (a <= b)                                                                                                        //Compare Job ID's of entered record and parent
//            {
//                return new ResponseEntity("Designation can not be same or higher", HttpStatus.BAD_REQUEST);
//            }
//            if (user.getDesi().equals("Intern")) {
//                return new ResponseEntity("Intern can not replace anyone", HttpStatus.BAD_REQUEST);
//            }
            repository.save(up);
        }
        return new ResponseEntity(up, HttpStatus.OK);
    }
}
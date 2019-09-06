package com.codegym.controller;

import com.codegym.model.Customer;
import com.codegym.service.CustomerService;
import com.codegym.service.CustomerServiceImpl;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@WebServlet(name = "CustomerServlet", urlPatterns = "/customers")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
        maxFileSize = 1024 * 1024 * 10, // 10MB
        maxRequestSize = 1024 * 1024 * 50) // 50MB
public class CustomerServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    public static final String SAVE_DIRECTORY = "images";

    public CustomerServlet() {
        super();
    }

    private CustomerService customerService = new CustomerServiceImpl();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String action = request.getParameter("action");
            if (action == null) {
                action = "";
            }
            switch (action) {
                case "create": {
                    createCustomer(request, response);
                    break;
                }
                case "edit": {
                    editCustomer(request, response);
                    break;
                }
                case "delete": {
                    deleteCustomer(request, response);
                    break;
                }

                default: {
                    searchCustomer(request, response);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error: " + e.getMessage());
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("customer/create.jsp");
            dispatcher.forward(request, response);
        }
    }

    private void searchCustomer(HttpServletRequest request, HttpServletResponse response) {
        String search = request.getParameter("txtSearch");
        List<Customer> lstCustomer = this.customerService.findAll();
        List<Customer> resCustomer = new ArrayList<>();

        for (Customer item : lstCustomer) {
            if (item.getName().equals(search)) {
                resCustomer.add(item);
            }
        }

        request.setAttribute("customers", resCustomer);
        RequestDispatcher dispatcher = request.getRequestDispatcher("customer/list.jsp");

        try {
            dispatcher.forward(request, response);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void deleteCustomer(HttpServletRequest request, HttpServletResponse response) {
        int id = Integer.parseInt(request.getParameter("id"));
        Customer customer = this.customerService.findById(id);

        RequestDispatcher dispatcher;
        if (customer == null) {
            dispatcher = request.getRequestDispatcher("error-404.jsp");
        } else {

            this.customerService.remove(id);

            request.setAttribute("message", "Delete customer successes!!!");
            request.setAttribute("customer", customer);
            dispatcher = request.getRequestDispatcher("customer/delete.jsp");
        }

        try {
            dispatcher.forward(request, response);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void editCustomer(HttpServletRequest request, HttpServletResponse response) {
        int id = Integer.parseInt(request.getParameter("id"));
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String address = request.getParameter("address");

        Customer customer = this.customerService.findById(id);

        RequestDispatcher dispatcher;
        if (customer == null) {
            dispatcher = request.getRequestDispatcher("error-404.jsp");
        } else {
            customer.setName(name);
            customer.setEmail(email);
            customer.setAddress(address);
            this.customerService.update(id, customer);

            request.setAttribute("customer", customer);
            request.setAttribute("message", "Update customer successes!!!");
            dispatcher = request.getRequestDispatcher("customer/edit.jsp");
        }

        try {
            dispatcher.forward(request, response);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createCustomer(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {

            // Đường dẫn tuyệt đối tới thư mục gốc của web app.
            //String appPath = request.getServletContext().getRealPath("");
            //appPath = appPath.replace('\\', '/');



            String avatar = "";

            // Đường dẫn tuyệt đối tới thư mục gốc của web app.
            String appPath = request.getServletContext().getRealPath("");
            appPath = appPath.replace('\\', '/');


            // Thư mục để save file tải lên.
            String fullSavePath = null;
            if (appPath.endsWith("/")) {
                fullSavePath = appPath + SAVE_DIRECTORY;
            } else {
                fullSavePath = appPath + "/" + SAVE_DIRECTORY;
            }


            // Tạo thư mục nếu nó không tồn tại.
            File fileSaveDir = new File(fullSavePath);
            if (!fileSaveDir.exists()) {
                fileSaveDir.mkdir();
            }

            // Danh mục các phần đã upload lên (Có thể là nhiều file).
            for (Part part : request.getParts()) {
                String fileName = extractFileName(part);
                if (fileName != null && fileName.length() > 0) {
                    String filePath = fullSavePath + File.separator + fileName;
                    System.out.println("Write attachment to file: " + filePath);

                    // Ghi vào file.
                    part.write(filePath);
                    avatar = fileName;
                }
            }


            String name = request.getParameter("name");
            String email = request.getParameter("email");
            String address = request.getParameter("address");

            int id = (int) (Math.random() * 10000);

            Customer customer = new Customer(id, name, email, address, avatar);
            this.customerService.save(customer);

            RequestDispatcher dispatcher = request.getRequestDispatcher("customer/create.jsp");
            request.setAttribute("m", "A new customer created!!!");
            try {
                dispatcher.forward(request, response);
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error: " + e.getMessage());
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("customer/create.jsp");
            dispatcher.forward(request, response);
        }
    }

    private String extractFileName(Part part) {
        // form-data; name="file"; filename="C:\file1.zip"
        // form-data; name="file"; filename="C:\Note\file2.zip"
        String contentDisp = part.getHeader("content-disposition");
        String[] items = contentDisp.split(";");
        for (String s : items) {
            if (s.trim().startsWith("filename")) {
                // C:\file1.zip
                // C:\Note\file2.zip
                String clientFileName = s.substring(s.indexOf("=") + 2, s.length() - 1);
                clientFileName = clientFileName.replace("\\", "/");
                int i = clientFileName.lastIndexOf('/');
                // file1.zip
                // file2.zip
                return clientFileName.substring(i + 1);
            }
        }
        return null;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "";
        }
        switch (action) {
            case "create": {
                showCreateForm(request, response);
                break;
            }
            case "edit": {
                showUpdateForm(request, response);
                break;
            }
            case "delete": {
                showDeleteForm(request, response);
                break;
            }
            default: {
                listCustomer(request, response);
                break;
            }
        }
    }

    private void showDeleteForm(HttpServletRequest request, HttpServletResponse response) {
        int id = Integer.parseInt(request.getParameter("id"));

        Customer customer = this.customerService.findById(id);

        request.setAttribute("customer", customer);
        RequestDispatcher dispatcher = request.getRequestDispatcher("customer/delete.jsp");

        try {
            dispatcher.forward(request, response);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void showUpdateForm(HttpServletRequest request, HttpServletResponse response) {
        int id = Integer.parseInt(request.getParameter("id"));
        Customer customer = this.customerService.findById(id);

        request.setAttribute("customer", customer);
        RequestDispatcher dispatcher = request.getRequestDispatcher("customer/edit.jsp");

        try {
            dispatcher.forward(request, response);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showCreateForm(HttpServletRequest request, HttpServletResponse response) {
        RequestDispatcher dispatcher = request.getRequestDispatcher("customer/create.jsp");
        try {
            dispatcher.forward(request, response);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void listCustomer(HttpServletRequest request, HttpServletResponse response) {
        List<Customer> customers = this.customerService.findAll();

        request.setAttribute("customers", customers);
        RequestDispatcher dispatcher = request.getRequestDispatcher("customer/list.jsp");

        try {
            dispatcher.forward(request, response);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

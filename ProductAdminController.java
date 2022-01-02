package music.admin;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import music.business.Product;
import music.data.ProductDB;

public class ProductAdminController extends HttpServlet {
    
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        String url = "/admin";
        if (requestURI.endsWith("/displayProducts")) {
            url = displayProducts(request, response);
        } else if (requestURI.endsWith("/displayProduct")) {
            url = displayProduct(request, response);
        } else if (requestURI.endsWith("/addProduct")) {
            url = "/admin/product.jsp";
        } else if (requestURI.endsWith("/updateProduct")) {
            updateProduct(request, response);
        } else if (requestURI.endsWith("/deleteProduct")) {
            url = deleteProduct(request, response);
        } else  {
            response.setStatus(404);
        }

        getServletContext()
                .getRequestDispatcher(url)
                .forward(request, response);
        
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        String url = "/admin";
        if (requestURI.endsWith("/displayProducts")) {
            url = displayProducts(request, response);
        } else if (requestURI.endsWith("/displayProduct")) {
            url = displayProduct(request, response);
        } else if (requestURI.endsWith("/updateProduct")) {
            updateProduct(request, response);
        }  else if (requestURI.endsWith("/addProduct")) {
            url = "/admin/product.jsp";
        } else if (requestURI.endsWith("/deleteProduct")) {
            url = deleteProduct(request, response);
        }  else  {
            response.setStatus(404);
        }

        getServletContext()
                .getRequestDispatcher(url)
                .forward(request, response);
       
    }

    private String displayProducts(HttpServletRequest request,
            HttpServletResponse response) {

        String url;
        List<Product> products = ProductDB.selectProducts();
        HttpSession session = request.getSession();
        session.setAttribute("products", products);
        url = "/admin/products.jsp";
        return url;
        
    }

    private String displayProduct(HttpServletRequest request,
            HttpServletResponse response) {

        HttpSession session = request.getSession();

        String productIdString = request.getParameter("productCode");
        //int productId = Integer.parseInt(productIdString);
        List<Product> products = (List<Product>) 
                session.getAttribute("products");

        Product prod = null;
        for (Product product : products) {
            prod = product;
            if (prod.getCode().equals(productIdString) ) {
                break;
            }
        }

        session.setAttribute("product", prod);

        return "/admin/product.jsp";
        
    }

    private String updateProduct(HttpServletRequest request,
            HttpServletResponse response) {

        HttpSession session = request.getSession();
        
        String productCode = (String) request.getParameter("productCode");
        String description = (String) request.getParameter("description");
        String priceString = (String) request.getParameter("price");

        double price;
        try {
            price = Double.parseDouble(priceString);
        } catch (NumberFormatException e) {
            price = 0;
        }

        Product product = (Product) session.getAttribute("product");
        if (product == null) {
            product = new Product();
        }
        product.setCode(productCode);
        product.setDescription(description);
        product.setPrice(price);
        session.setAttribute("product", product);
        
        String message = "";
        if (product.getPrice() <= 0) {
            message = "You must enter a positive number for the price without "
                    + "any currency symbols.";
        }
        if (product.getDescription().isEmpty()) {
            message = "You must enter a description for the product.";
        }
        if (product.getCode().isEmpty()) {
            message = "You must enter a code for the product.";
        }
        session.setAttribute("message", message);
        
        String url;
        if (message.isEmpty()) {
            if (ProductDB.exists(product.getCode())) {
                ProductDB.updateProduct(product);
            } else {
                ProductDB.insertProduct(product);
            }
            url = displayProducts(request, response);
        } else {
            url = "/admin/product.jsp";
        }
        return url;
    }
    
    private String deleteProduct(HttpServletRequest request,
            HttpServletResponse response) {

        HttpSession session = request.getSession();
        
        String productCode = request.getParameter("productCode");
        Product product = ProductDB.selectProduct(productCode);
        session.setAttribute("product", product); 
        
        String url;
        String yesButton = request.getParameter("yesButton");
        String noButton = request.getParameter("noButton");
        if (yesButton != null) {
            ProductDB.deleteProduct(product);
            url = displayProducts(request, response);
        } else if (noButton != null){
            url = displayProducts(request, response);
        } else {
            url = "/admin/confirm_product_delete.jsp";
        }
        return url;
    }    
}
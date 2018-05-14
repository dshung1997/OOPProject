package controller;

import controller.cashflow.CashflowManager;
import controller.employees.EmployeesManager;
import controller.expenses.ExpensesManager;
import controller.inventory.InventoryManager;
import controller.login.LoginManager;
import controller.products.ProductsManager;
import controller.sales.SalesManager;
import model.employee.Employee;
import model.expense.Expense;
import model.product.*;
import javafx.collections.ObservableList;
import model.receipts.GoodsReceipt;
import model.receipts.ItemOrder;
import model.receipts.SellReceipt;

import java.io.*;
import java.time.LocalDate;
import java.util.regex.Pattern;

public class DataManager {

    private ProductsManager productsManager;
    private SalesManager salesManager;
    private InventoryManager inventoryManager;
    private ExpensesManager expensesManager;
    private CashflowManager cashflowManager;
    private EmployeesManager employeesManager;
    private LoginManager loginManager;

//        ClassLoader classLoader = getClass().getClassLoader();
//        File file = new File(classLoader.getResource("data/products").getFile());

    public DataManager()
    {
        setProductsManager(new ProductsManager());
        setSalesManager(new SalesManager());
        setInventoryManager(new InventoryManager());
        setExpensesManager(new ExpensesManager());
        setCashflowManager(new CashflowManager());
        setEmployeesManager(new EmployeesManager());
        setLoginManager(new LoginManager());

        //readData();
    }


    public void readData()
    {
        readProductsFile();
        readSellReceiptsFile();
        readGoodsReceiptsFile();
        readExpensesFile();
        readEmployeesFile();
    }

    public void writeData()
    {
        writeProductsFile();
        writeSellReceiptsFile();
        writeGoodsReceiptsFile();
        writeExpensesFile();
        writeEmployeesFile();
    }

    private void readProductsFile()
    {
        try {
            File file = new File(FinalPaths.DATA+"Products.txt");
            System.out.println(file.getAbsolutePath());
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line = bufferedReader.readLine();

            while(line != null)
            {
                System.out.println("\""+line+"\"");

                productsManager.addProduct(parseProduct(line));

                line = bufferedReader.readLine();
                if(line == null) break;
            }
            bufferedReader.close();
            fileReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeProductsFile()
    {
        //String comment = "# Category|productID|name|status|quantity|sellingPrice|buyingPrice|nation|imageURL|discount\n";

        try {
            File file = new File(FinalPaths.DATA+"Products.txt");
            FileWriter fileWriter = new FileWriter(file, false);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            ObservableList<Product> list = productsManager.getProducts();
            list.forEach(p -> {
                try {
                    bufferedWriter.write(p.toString());
                    bufferedWriter.write("\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            bufferedWriter.close();
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void writeGoodsReceiptsFile()
    {
        //String comment = "# Category|productID|name|status|quantity|sellingPrice|buyingPrice|nation|imageURL|discount\n";

        try {
            File file = new File(FinalPaths.DATA+"BuyReceipts.txt");
            FileWriter fileWriter = new FileWriter(file, false);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            ObservableList<GoodsReceipt> listGoodsReceipts = inventoryManager.getListGoodsReceipts();

            for(GoodsReceipt goodsReceipt : listGoodsReceipts)
            {
                bufferedWriter.write(goodsReceipt.toString());
                bufferedWriter.write("\n");
            }

            bufferedWriter.close();
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void readGoodsReceiptsFile()
    {
        try {
            File file = new File(FinalPaths.DATA+"BuyReceipts.txt");
            System.out.println(file.getAbsolutePath());
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line = bufferedReader.readLine();

            while(line != null)
            {
                System.out.println("\""+line+"\"");

                String[] parts = line.split(Pattern.quote("|"));

                GoodsReceipt goodsReceipt = new GoodsReceipt();
                goodsReceipt.setReceiptID(parts[0]);
                goodsReceipt.setCategory(Enum.valueOf(model.receipts.Category.class, parts[1]));
                goodsReceipt.setSupplierName(parts[2]);
                goodsReceipt.setPurchaserName(parts[3]);
                goodsReceipt.setTotalCost(Double.valueOf(parts[4]));
                goodsReceipt.setDate(LocalDate.parse(parts[5]));
                goodsReceipt.setRemark(App.asciiToString(parts[6]));

                int nItems = Integer.valueOf(parts[7]);
                for(int j = 0; j < nItems; j++)
                {
                    String rawItem = bufferedReader.readLine();
                    int first = rawItem.indexOf("|");
                    int amount = Integer.parseInt(rawItem.substring(0, first));

                    String rawProduct = rawItem.substring(first + 1);
                    Product product = Product.valueOf(rawProduct);

                    goodsReceipt.getListItems().add(new ItemOrder(product, amount));
                }

                inventoryManager.addGoodsReceipt(goodsReceipt);

                bufferedReader.readLine();
                line = bufferedReader.readLine();
                if(line == null) break;
            }
            bufferedReader.close();
            fileReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readExpensesFile()
    {
        try {
            File file = new File(FinalPaths.DATA+"Expenses.txt");
            System.out.println(file.getAbsolutePath());
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line = bufferedReader.readLine();

            while(line != null)
            {
                Expense e = Expense.valueOf(line);
                expensesManager.addExpense(e);
                line = bufferedReader.readLine();
                if(line == null) break;
            }
            bufferedReader.close();
            fileReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeExpensesFile()
    {
        try {
            File file = new File(FinalPaths.DATA+"Expenses.txt");
            FileWriter fileWriter = new FileWriter(file, false);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            ObservableList<Expense> listExpenses = expensesManager.getListExpenses();

            for(Expense expense: listExpenses)
            {
                bufferedWriter.write(expense.toString());
                bufferedWriter.write("\n");
            }

            bufferedWriter.close();
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void writeSellReceiptsFile()
    {
        //String comment = "# Category|productID|name|status|quantity|sellingPrice|buyingPrice|nation|imageURL|discount\n";

        try {
            File file = new File(FinalPaths.DATA+"SellReceipts.txt");
            FileWriter fileWriter = new FileWriter(file, false);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            ObservableList<SellReceipt> listSellReceipts = salesManager.getListSellReceipts();

            for(SellReceipt r: listSellReceipts)
            {
                bufferedWriter.write(r.toString());
                bufferedWriter.write("\n");
            }

            bufferedWriter.close();
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void readSellReceiptsFile()
    {
        try {
            File file = new File(FinalPaths.DATA+"SellReceipts.txt");
            System.out.println(file.getAbsolutePath());
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line = bufferedReader.readLine();

            while(line != null)
            {
                System.out.println("\""+line+"\"");

                String[] parts = line.split(Pattern.quote("|"));

                SellReceipt r = new SellReceipt();
                r.setReceiptID(parts[0]);
                r.setCategory(Enum.valueOf(model.receipts.Category.class, parts[1]));
                r.setCustomerName(parts[2]);
                r.setCashierName(parts[3]);
                r.setTotalCost(Double.valueOf(parts[4]));
                r.setDate(LocalDate.parse(parts[5]));
                r.setRemark(App.asciiToString(parts[6]));

                int nItems = Integer.valueOf(parts[7]);
                for(int i = 0; i < nItems; i++)
                {
                    String rawItem = bufferedReader.readLine();
                    int first = rawItem.indexOf("|");
                    int amount = Integer.parseInt(rawItem.substring(0, first));

                    String rawProduct = rawItem.substring(first + 1);
                    Product product = Product.valueOf(rawProduct);

                    r.getListItems().add(new ItemOrder(product, amount));
                }

                salesManager.addSellReceipt(r);

                line = bufferedReader.readLine();
                line = bufferedReader.readLine();
                if(line == null) break;
            }
            bufferedReader.close();
            fileReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Parse product from Products.txt
    public Product parseProduct1(String line)
    {
        String[] parts = line.split(Pattern.quote("|"));

        Product p = new Product();
        p.setProductID(parts[1]);
        p.setName(parts[2]);
        p.setCategory(Category.valueOf(parts[0]));
        p.setStatus(Status.valueOf(parts[3]));
        p.setQuantity(Integer.valueOf(parts[4]));
        p.setBuyingPrice(Double.valueOf(parts[5]));
        p.setSellingPrice(Double.valueOf(parts[6]));
        p.setNation(Nation.valueOf(parts[7]));
        p.setImageUrl(parts[8]);
        p.setDiscount(Integer.valueOf(parts[9]));

        int curPos = 10;

        if(Category.valueOf(parts[0]) == Category.BOOK)
        {
            Book b = new Book(p);

            int nAuthors = Integer.valueOf(parts[curPos]);
            curPos += 1;
            for(int i = 0; i < nAuthors; i++)
            {
                b.getListAuthors().add(parts[curPos]);
                curPos += 1;
            }

            b.setLanguage(Language.valueOf(parts[curPos])); curPos += 1;
            b.setGenre(BookGenre.valueOf(parts[curPos])); curPos += 1;
            b.setLength(Integer.valueOf(parts[curPos])); curPos += 1;
            b.setPublicDate(LocalDate.parse(parts[curPos]));

            return b;
        }
        else if(Category.valueOf(parts[0]) == Category.MOVIE_DISC)
        {
            MovieDisc m = new MovieDisc(p);

            int nActors = Integer.valueOf(parts[curPos]);
            curPos += 1;
            for(int i = 0; i < nActors; i++)
            {
                m.getListActors().add(parts[curPos]);
                curPos += 1;
            }

            m.setDirector(parts[curPos]); curPos += 1;
            m.setLanguage(Language.valueOf(parts[curPos])); curPos += 1;
            m.setSubtitle(Language.valueOf(parts[curPos])); curPos += 1;
            m.setGenre(MovieGenre.valueOf(parts[curPos])); curPos += 1;
            m.setLength(Integer.valueOf(parts[curPos])); curPos += 1;
            m.setImdbPoint(Float.valueOf(parts[curPos])); curPos += 1;
            m.setPublicDate(LocalDate.parse(parts[curPos]));

            return m;
        }
        else
        {
            MusicDisc m = new MusicDisc(p);

            int nSingers = Integer.valueOf(parts[curPos]);
            curPos += 1;
            for(int i = 0; i < nSingers; i++)
            {
                m.getListSingers().add(parts[curPos]);
                curPos += 1;
            }
            m.setLanguage(Language.valueOf(parts[curPos])); curPos += 1;
            m.setGenre(MusicGenre.valueOf(parts[curPos])); curPos += 1;
            m.setPublicDate(LocalDate.parse(parts[curPos]));

            return m;
        }
    }

    public Product parseProduct(String line)
    {
        int first = line.indexOf("|");
        String category = line.substring(0, first);

        if(Category.valueOf(category) == Category.BOOK)
            return (Product) Book.valueOf(line);

        else if(Category.valueOf(category) == Category.MOVIE_DISC)
            return (Product) MovieDisc.valueOf(line);
        else
            return (Product) MusicDisc.valueOf(line);
    }

    private void readEmployeesFile()
    {
        try {
            File file = new File(FinalPaths.DATA+"Employees.txt");
            System.out.println(file.getAbsolutePath());
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line = bufferedReader.readLine();

            while(line != null)
            {
                Employee e = Employee.valueOf(line);
                employeesManager.addEmployee(e);
                line = bufferedReader.readLine();
                if(line == null) break;
            }
            bufferedReader.close();
            fileReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeEmployeesFile()
    {
        try {
            File file = new File(FinalPaths.DATA+"Employees.txt");
            FileWriter fileWriter = new FileWriter(file, false);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            ObservableList<Employee> listEmployees = employeesManager.getListEmployees();

            for(Employee e: listEmployees)
            {
                bufferedWriter.write(e.toString());
                bufferedWriter.write("\n");
            }

            bufferedWriter.close();
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    public void setInventoryManager(InventoryManager inventoryManager) {
        this.inventoryManager = inventoryManager;
    }

    public ExpensesManager getExpensesManager() {
        return expensesManager;
    }

    public void setExpensesManager(ExpensesManager expensesManager) {
        this.expensesManager = expensesManager;
    }

    public void setCashflowManager(CashflowManager cashflowManager) {
        this.cashflowManager = cashflowManager;
    }

    public ProductsManager getProductsManager() {
        return productsManager;
    }

    public void setProductsManager(ProductsManager productsManager) {
        this.productsManager = productsManager;
    }

    public CashflowManager getCashflowManager() {
        return cashflowManager;
    }

    public SalesManager getSalesManager() {
        return salesManager;
    }

    public void setSalesManager(SalesManager salesManager) {
        this.salesManager = salesManager;
    }

    public EmployeesManager getEmployeesManager() {
        return employeesManager;
    }

    public void setEmployeesManager(EmployeesManager employeesManager) {
        this.employeesManager = employeesManager;
    }

    public LoginManager getLoginManager() {
        return loginManager;
    }

    public void setLoginManager(LoginManager loginManager) {
        this.loginManager = loginManager;
    }
}
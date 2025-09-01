# Pahana Edu Bookshop Management System

A comprehensive, distributed web-based bookshop management system built with **Spring Boot** (backend) and **React.js** (frontend) with **MySQL** database.

## 🏗️ System Architecture

- **Backend**: Java 17, Spring Boot 3.x, Spring Security, Spring Data JPA
- **Frontend**: React.js 18, TypeScript, Material-UI
- **Database**: MySQL 8.x
- **Authentication**: JWT-based stateless authentication
- **Architecture**: Microservices with REST APIs

## ✨ Features

### 🔐 Authentication & Authorization
- JWT-based secure authentication
- Role-based access control (Admin, Manager, Employee, Viewer)
- Permission-based authorization
- Account lockout mechanism

### 👥 Customer Management
- Customer registration with auto-generated account numbers
- CRUD operations for customer data
- Search and filter functionality
- Customer bill history tracking

### 📦 Inventory Management
- Item management with categories
- Stock level monitoring and alerts
- Low stock notifications
- Price management and tracking

### 💰 Billing System
- Create and manage bills/invoices
- Automatic tax calculations (15% VAT)
- Discount support at item level
- Bill status workflow (Draft, Pending, Paid, Cancelled)
- Stock adjustment during billing

### 📊 Reporting & Analytics
- Dashboard with key metrics
- Sales reports and analytics
- Inventory reports
- Customer analytics

## 🚀 Quick Start

### Prerequisites

- **Java 17+**
- **Node.js 16+**
- **MySQL 8.x**
- **Maven 3.6+**

### Database Setup

1. Create MySQL database:
```sql
CREATE DATABASE pahana_bookshop;
```

2. Run the database schema script:
```bash
mysql -u root -p pahana_bookshop < database/schema.sql
```

### Backend Setup

1. Navigate to backend directory:
```bash
cd backend
```

2. Configure database connection in `src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/pahana_bookshop
    username: your_username
    password: your_password
```

3. Build and run the backend:
```bash
mvn clean install
mvn spring-boot:run
```

The backend will start on `http://localhost:8080`

### Frontend Setup

1. Navigate to frontend directory:
```bash
cd frontend
```

2. Install dependencies:
```bash
npm install
```

3. Start the development server:
```bash
npm start
```

The frontend will start on `http://localhost:3000`

## 🔑 Default Login Credentials

| Role | Username | Password |
|------|----------|----------|
| Admin | admin | admin123 |
| Manager | manager | manager123 |
| Employee | employee | employee123 |

## 📁 Project Structure

```
pahana-edu-bookshop/
├── backend/                          # Spring Boot backend
│   ├── src/main/java/com/pahana/edu/bookshop/
│   │   ├── controller/               # REST controllers
│   │   ├── service/                  # Business logic services
│   │   ├── repository/               # Data access repositories
│   │   ├── entity/                   # JPA entities
│   │   ├── dto/                      # Data Transfer Objects
│   │   ├── security/                 # Security configuration
│   │   ├── exception/                # Exception handling
│   │   └── enums/                    # Enumerations
│   ├── src/main/resources/
│   │   ├── application.yml           # Application configuration
│   │   └── application-test.yml      # Test configuration
│   └── pom.xml                       # Maven dependencies
├── frontend/                         # React.js frontend
│   ├── src/
│   │   ├── components/               # Reusable components
│   │   ├── pages/                    # Page components
│   │   ├── services/                 # API services
│   │   ├── store/                    # State management (Zustand)
│   │   ├── types/                    # TypeScript type definitions
│   │   ├── utils/                    # Utility functions
│   │   ├── constants/                # Constants and configurations
│   │   └── theme/                    # Material-UI theme
│   ├── public/                       # Static assets
│   └── package.json                  # NPM dependencies
├── database/
│   └── schema.sql                    # Database schema and sample data
└── README.md                         # Project documentation
```

## 🏛️ Database Schema

### Core Tables

- **users**: System users with authentication and authorization
- **customers**: Customer accounts and information
- **items**: Inventory items with pricing and stock
- **bills**: Customer bills/invoices
- **bill_items**: Individual items within bills

### Key Relationships

- Customer → Bills (One-to-Many)
- Bill → BillItems (One-to-Many)
- Item → BillItems (One-to-Many)

## 🛡️ Security Features

- **JWT Authentication**: Stateless token-based authentication
- **Role-based Access**: Different permission levels for different roles
- **Password Encryption**: BCrypt password hashing
- **Account Lockout**: Protection against brute force attacks
- **CORS Configuration**: Secure cross-origin requests

## 🔧 API Documentation

Once the backend is running, access the Swagger UI documentation at:
`http://localhost:8080/swagger-ui.html`

### Key API Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/auth/login` | POST | User authentication |
| `/api/customers` | GET/POST | Customer management |
| `/api/items` | GET/POST | Item management |
| `/api/bills` | GET/POST | Bill management |
| `/api/reports` | GET | Reports and analytics |

## 🧪 Testing

### Backend Testing
```bash
cd backend
mvn test
```

### Frontend Testing
```bash
cd frontend
npm test
```

## 🏗️ Design Patterns Implemented

- **Repository Pattern**: Data access abstraction
- **Service Layer Pattern**: Business logic separation
- **DTO Pattern**: API data contracts
- **Builder Pattern**: Entity construction
- **Strategy Pattern**: Billing calculations
- **Observer Pattern**: Event-driven notifications
- **MVC Pattern**: Clear separation of concerns

## 📊 Performance Considerations

- **Database Indexing**: Optimized queries with proper indexes
- **Pagination**: Efficient data loading with pagination
- **Caching**: Response caching for better performance
- **Connection Pooling**: Efficient database connections
- **Lazy Loading**: Optimized JPA relationships

## 🔄 Business Rules

### Customer Management
- Account numbers are auto-generated and unique
- Phone numbers must follow Sri Lankan format
- Email addresses must be unique
- Customers with bills cannot be deleted

### Inventory Management
- Item codes must be unique and follow naming convention
- Stock quantities cannot be negative
- Low stock alerts when quantity < minimum threshold
- Price changes are tracked with audit trail

### Billing System
- Bills must have at least one item
- Stock availability check before bill creation
- Automatic tax calculation (15% VAT)
- Sequential bill numbering
- Stock reduction after successful bill creation

## 🚀 Deployment

### Production Build

**Backend:**
```bash
cd backend
mvn clean package
java -jar target/bookshop-backend-1.0.0.jar
```

**Frontend:**
```bash
cd frontend
npm run build
```

### Environment Variables

Set the following environment variables for production:

```bash
# Database
DB_USERNAME=your_db_username
DB_PASSWORD=your_db_password

# JWT
JWT_SECRET=your_jwt_secret_key

# CORS
CORS_ORIGINS=https://yourdomain.com
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🏢 About Pahana Edu

This system is designed for Pahana Edu, a leading bookshop in Colombo City, serving hundreds of customers each month with educational materials and supplies.

## 🛟 Support

For support and questions, please contact:
- Email: support@pahanaedu.com
- Phone: +94 11 234 5678

---

**Built with ❤️ for education and learning**
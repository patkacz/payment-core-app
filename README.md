# Payment Terminal Application

A modern Android payment terminal application written in Kotlin, designed to handle various payment processing scenarios with different terminal vendors and payment processors.

## Architecture

The application follows Clean Architecture principles and MVVM pattern, with a clear separation of concerns:
```
app/src/main/java/com/flatpay/payment_core_app/
├── common
    └── workflows      # Transaction workflows logic
├── data/              # Data layer: implementations, repositories
│   ├── models/        # Data models
│   ├── repositories/  # Repository implementations
│   ├── host/         # Host communication implementations
│   ├── mock/         # Mock implementations for testing
│   └── config/       # Configuration classes
├── domain/           # Business logic layer
│   ├── interfaces/   # Core interfaces
│   ├── usecases/    # Business logic use cases
│   └── workflows/   # Transaction workflows
├── viewmodels/  # ViewModels
└── ui/          # Activities and Fragments

```
### Key Components

- **ITerminal**: Interface for terminal hardware communication
- **IHostCommunication**: Interface for payment host communication
- **HostRepository**: Manages payment processing and transaction state
- **PaymentViewModel**: Handles UI state and business logic coordination
- **Transaction Workflows**: Implements different transaction types (sale, refund, void)

### Design Patterns

- **MVVM**: Separation of UI and business logic
- **Repository Pattern**: Abstract data sources
- **Factory Pattern**: For ViewModel creation
- **Strategy Pattern**: For different terminal implementations
- **Observer Pattern**: For UI state updates using LiveData

## Technology Stack

- **Language**: Kotlin
- **Minimum SDK**: 27 (Android 8.1)
- **Architecture Components**:
    - 
    - 
    - 
- **Concurrency**: Kotlin 
- **Payment Processing**:  
- **Testing**: 

## Implemented Features

### Core Framework
- [ ] Basic MVVM architecture setup
- [ ] Terminal interface definition
- [ ] Host communication interface
- [ ] Basic transaction workflow
- [ ] Amount input and validation
- [ ] Progress dialog handling
- [ ] Error handling and user feedback

### Payment Processing
- [ ] Sale transaction flow
- [ ] Transaction response handling
- [ ] Basic error handling
- [ ] Mock terminal implementation for testing

### UI/UX
- [ ] Amount input screen
- [ ] Processing dialog
- [ ] Basic error messages
- [ ] Transaction result display

## TODO

### Core Features
- [ ] EMV configuration management
- [ ] Transaction logging
- [ ] Offline mode support
- [ ] Receipt printing implementation
- [ ] Transaction history storage

### Payment Features
- [ ] Refund workflow
- [ ] Pre-authorization
- [ ] Multi-currency support

### Terminal Integration
- [ ] Real terminal integration
- [ ] Multiple terminal vendor support
- [ ] Terminal status monitoring
- [ ] Hardware event handling

### Host Communication
- [ ] Settlement handling
- [ ] Webhook handling

### Security
- [ ] Key management
- [ ] Secure storage implementation
- [ ] PIN handling
- [ ] Transaction encryption
- [ ] Security logging

### Testing
- [ ] Unit test coverage
- [ ] Integration tests
- [ ] UI tests
- [ ] Mock card data for testing
- [ ] Performance testing

### UI/UX Improvements
- [ ] Transaction details screen
- [ ] Settings screen
- [ ] Terminal configuration UI
- [ ] Transaction history view
- [ ] Receipt preview
- [ ] Better error handling UI

## Getting Started

### Prerequisites
- Android Studio 
- Android SDK 24 or higher
- Kotlin 1.8.0 or higher

### Setup
1. Clone the repository
2. Open project in Android Studio
3. Sync project with Gradle files
4. Run the application

### Configuration
```
```


# Testing
## Running Tests
```bash
# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest
```
# Mock Implementation


#!/usr/bin/env node

/**
 * Simple Node.js script to test customer API endpoints
 * This script validates that the customer CRUD operations work correctly
 */

const axios = require('axios');

const API_BASE_URL = 'http://localhost:8080/api';
const TEST_CREDENTIALS = {
  username: 'admin',
  password: 'admin123'
};

let authToken = '';

// Test customer data
const testCustomer = {
  name: 'Test Customer',
  address: '123 Test Street, Test City, Sri Lanka',
  phone: '+94771234567',
  email: 'test@example.com'
};

async function authenticate() {
  try {
    console.log('🔐 Authenticating as admin...');
    const response = await axios.post(`${API_BASE_URL}/auth/login`, TEST_CREDENTIALS);
    
    if (response.data.success) {
      authToken = response.data.data.accessToken;
      console.log('✅ Authentication successful');
      return true;
    } else {
      console.log('❌ Authentication failed:', response.data.message);
      return false;
    }
  } catch (error) {
    console.log('❌ Authentication error:', error.message);
    return false;
  }
}

async function testCustomerCRUD() {
  const headers = {
    'Authorization': `Bearer ${authToken}`,
    'Content-Type': 'application/json'
  };

  let customerId = null;

  try {
    // Test 1: Create Customer
    console.log('\n📝 Testing customer creation...');
    const createResponse = await axios.post(`${API_BASE_URL}/customers`, testCustomer, { headers });
    
    if (createResponse.data.success) {
      customerId = createResponse.data.data.id;
      console.log('✅ Customer created successfully:', createResponse.data.data.name);
      console.log('   Account Number:', createResponse.data.data.accountNumber);
    } else {
      console.log('❌ Customer creation failed:', createResponse.data.message);
      return;
    }

    // Test 2: Read Customer
    console.log('\n📖 Testing customer retrieval...');
    const readResponse = await axios.get(`${API_BASE_URL}/customers/${customerId}`, { headers });
    
    if (readResponse.data.success) {
      console.log('✅ Customer retrieved successfully:', readResponse.data.data.name);
    } else {
      console.log('❌ Customer retrieval failed:', readResponse.data.message);
    }

    // Test 3: Update Customer
    console.log('\n✏️  Testing customer update...');
    const updatedCustomer = {
      ...testCustomer,
      name: 'Updated Test Customer',
      phone: '+94771234568'
    };
    
    const updateResponse = await axios.put(`${API_BASE_URL}/customers/${customerId}`, updatedCustomer, { headers });
    
    if (updateResponse.data.success) {
      console.log('✅ Customer updated successfully:', updateResponse.data.data.name);
    } else {
      console.log('❌ Customer update failed:', updateResponse.data.message);
    }

    // Test 4: List Customers
    console.log('\n📋 Testing customer list...');
    const listResponse = await axios.get(`${API_BASE_URL}/customers?page=0&size=10`, { headers });
    
    if (listResponse.data.success) {
      const customerCount = listResponse.data.data.pagination.totalElements;
      console.log(`✅ Customer list retrieved successfully: ${customerCount} customers found`);
    } else {
      console.log('❌ Customer list failed:', listResponse.data.message);
    }

    // Test 5: Search Customers
    console.log('\n🔍 Testing customer search...');
    const searchResponse = await axios.get(`${API_BASE_URL}/customers/search?query=Updated&page=0&size=10`, { headers });
    
    if (searchResponse.data.success) {
      const searchCount = searchResponse.data.data.content.length;
      console.log(`✅ Customer search successful: ${searchCount} customers found`);
    } else {
      console.log('❌ Customer search failed:', searchResponse.data.message);
    }

    // Test 6: Delete Customer
    console.log('\n🗑️  Testing customer deletion...');
    const deleteResponse = await axios.delete(`${API_BASE_URL}/customers/${customerId}`, { headers });
    
    if (deleteResponse.data.success) {
      console.log('✅ Customer deleted successfully');
    } else {
      console.log('❌ Customer deletion failed:', deleteResponse.data.message);
    }

  } catch (error) {
    console.log('❌ CRUD test error:', error.response?.data?.message || error.message);
    
    // Cleanup: Try to delete the test customer if it was created
    if (customerId) {
      try {
        await axios.delete(`${API_BASE_URL}/customers/${customerId}`, { headers });
        console.log('🧹 Test customer cleaned up');
      } catch (cleanupError) {
        console.log('⚠️  Could not cleanup test customer');
      }
    }
  }
}

async function testAccessControl() {
  console.log('\n🔒 Testing access control...');
  
  try {
    // Test without authentication
    const response = await axios.get(`${API_BASE_URL}/customers`);
    console.log('❌ Unauthenticated access should have failed');
  } catch (error) {
    if (error.response?.status === 401) {
      console.log('✅ Unauthenticated access properly blocked');
    } else {
      console.log('❌ Unexpected error:', error.message);
    }
  }
}

async function runTests() {
  console.log('🧪 Starting Customer Management API Tests');
  console.log('==========================================');
  
  // Test authentication
  const authSuccess = await authenticate();
  if (!authSuccess) {
    console.log('\n❌ Cannot proceed without authentication');
    return;
  }
  
  // Test access control
  await testAccessControl();
  
  // Test CRUD operations
  await testCustomerCRUD();
  
  console.log('\n🎉 All tests completed!');
  console.log('==========================================');
}

// Run the tests
if (require.main === module) {
  runTests().catch(console.error);
}

module.exports = { runTests };
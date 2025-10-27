import React, { useState, useEffect } from 'react';

const AdminDashboard = () => {
  const [activeTab, setActiveTab] = useState('overview');
  const [systemStats, setSystemStats] = useState({});
  const [users, setUsers] = useState([]);
  const [workflows, setWorkflows] = useState([]);
  const [integrations, setIntegrations] = useState([]);

  useEffect(() => {
    fetchSystemStats();
    fetchUsers();
    fetchWorkflows();
    fetchIntegrations();
  }, []);

  const fetchSystemStats = async () => {
    try {
      const response = await fetch('/api/analytics/executive-dashboard');
      const data = await response.json();
      setSystemStats(data);
    } catch (error) {
      console.error('Error fetching system stats:', error);
    }
  };

  const fetchUsers = async () => {
    try {
      const response = await fetch('/api/integrations/users');
      if (response.ok) {
        const data = await response.json();
        setUsers(Array.isArray(data) ? data : []);
      } else {
        throw new Error('Failed to fetch users');
      }
    } catch (error) {
      console.error('Error fetching users:', error);
      // Fallback to mock data
      setUsers([
        { id: 1, username: 'admin', email: 'admin@example.com', role: 'ADMIN', status: 'ACTIVE' },
        { id: 2, username: 'editor', email: 'editor@example.com', role: 'EDITOR', status: 'ACTIVE' },
        { id: 3, username: 'user1', email: 'user1@example.com', role: 'USER', status: 'ACTIVE' }
      ]);
    }
  };

  const fetchWorkflows = async () => {
    try {
      const response = await fetch('/api/workflows/analytics');
      const data = await response.json();
      setWorkflows(Object.entries(data.statusDistribution || {}));
    } catch (error) {
      console.error('Error fetching workflows:', error);
    }
  };

  const fetchIntegrations = async () => {
    try {
      const response = await fetch('/api/integrations/status');
      if (response.ok) {
        const data = await response.json();
        setIntegrations(Array.isArray(data) ? data : []);
      } else {
        throw new Error('Failed to fetch integrations');
      }
    } catch (error) {
      console.error('Error fetching integrations:', error);
      // Fallback to mock data
      setIntegrations([
        { name: 'Google Analytics', status: 'CONNECTED', lastSync: '2024-01-15 10:30' },
        { name: 'Facebook', status: 'CONNECTED', lastSync: '2024-01-15 09:15' },
        { name: 'Twitter', status: 'DISCONNECTED', lastSync: 'Never' },
        { name: 'Email Service', status: 'CONNECTED', lastSync: '2024-01-15 11:00' }
      ]);
    }
  };

  // User Management Functions
  const handleAddUser = () => {
    const username = prompt('Enter username:');
    const email = prompt('Enter email:');
    const password = prompt('Enter password:');
    const role = prompt('Enter role (USER/EDITOR/ADMIN):') || 'USER';
    
    if (username && email && password) {
      fetch('/api/integrations/users', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, email, password, role })
      })
      .then(response => {
        if (response.ok) {
          alert('User added successfully');
          fetchUsers();
        } else {
          response.text().then(text => {
            console.error('Server error:', text);
            alert('Error adding user: ' + (text || 'Unknown error'));
          });
        }
      })
      .catch(error => {
        console.error('Error adding user:', error);
        alert('Error adding user');
      });
    }
  };

  const handleEditUser = (userId) => {
    const user = users.find(u => u.id === userId);
    const newRole = prompt('Enter new role:', user.role);
    
    if (newRole) {
      fetch(`/api/users/${userId}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ role: newRole })
      })
      .then(() => {
        alert('User updated successfully');
        fetchUsers();
      })
      .catch(error => {
        console.error('Error updating user:', error);
        alert('Error updating user');
      });
    }
  };

  const handleSuspendUser = (userId) => {
    if (window.confirm('Are you sure you want to suspend this user?')) {
      fetch(`/api/integrations/users/${userId}/suspend`, {
        method: 'PUT'
      })
      .then(response => {
        if (response.ok) {
          alert('User suspended successfully');
          fetchUsers();
        } else {
          response.text().then(text => {
            console.error('Suspend error:', text);
            alert('Error suspending user: ' + (text || 'Unknown error'));
          });
        }
      })
      .catch(error => {
        console.error('Error suspending user:', error);
        alert('Error suspending user');
      });
    }
  };

  // Integration Management Functions
  const handleConnectIntegration = (integrationName) => {
    fetch(`/api/integrations/connect?integrationName=${integrationName}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ config: {} })
    })
    .then(response => response.json())
    .then(data => {
      alert(data.message);
      fetchIntegrations();
    })
    .catch(error => {
      console.error('Error connecting integration:', error);
      alert('Error connecting integration');
    });
  };

  const handleDisconnectIntegration = (integrationName) => {
    if (window.confirm(`Are you sure you want to disconnect ${integrationName}?`)) {
      fetch(`/api/integrations/disconnect?integrationName=${integrationName}`, {
        method: 'POST'
      })
      .then(response => response.json())
      .then(data => {
        alert(data.message);
        fetchIntegrations();
      })
      .catch(error => {
        console.error('Error disconnecting integration:', error);
        alert('Error disconnecting integration');
      });
    }
  };

  const handleTestIntegration = (integrationName) => {
    fetch(`/api/integrations/test?integrationName=${integrationName}`, {
      method: 'POST'
    })
    .then(response => response.json())
    .then(data => {
      alert(`Test result: ${data.message} (${data.responseTime})`);
    })
    .catch(error => {
      console.error('Error testing integration:', error);
      alert('Error testing integration');
    });
  };

  // Workflow Management Functions
  const handleCreateWorkflow = () => {
    const workflowName = prompt('Enter workflow name:');
    const workflowType = prompt('Enter workflow type:');
    
    if (workflowName && workflowType) {
      fetch(`/api/workflows/custom?workflowName=${workflowName}&workflowType=${workflowType}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ configuration: {} })
      })
      .then(() => {
        alert('Workflow created successfully');
        fetchWorkflows();
      })
      .catch(error => {
        console.error('Error creating workflow:', error);
        alert('Error creating workflow');
      });
    }
  };

  const renderOverview = () => (
    <div className="admin-overview">
      <div className="stats-grid">
        <div className="stat-card">
          <h3>Total Users</h3>
          <div className="stat-value">{users.length}</div>
          <div className="stat-change">+12% this month</div>
        </div>
        <div className="stat-card">
          <h3>Total Blogs</h3>
          <div className="stat-value">{systemStats.totalBlogs || 0}</div>
          <div className="stat-change">+8% this week</div>
        </div>
        <div className="stat-card">
          <h3>Total Views</h3>
          <div className="stat-value">{systemStats.totalViews || 0}</div>
          <div className="stat-change">+15% this month</div>
        </div>
        <div className="stat-card">
          <h3>System Health</h3>
          <div className="stat-value health-good">99.9%</div>
          <div className="stat-change">Uptime</div>
        </div>
      </div>

      <div className="recent-activity">
        <h3>Recent System Activity</h3>
        <div className="activity-list">
          <div className="activity-item">
            <span className="activity-time">10:30 AM</span>
            <span className="activity-desc">New user registration: user@example.com</span>
          </div>
          <div className="activity-item">
            <span className="activity-time">10:15 AM</span>
            <span className="activity-desc">Blog published: "Advanced React Patterns"</span>
          </div>
          <div className="activity-item">
            <span className="activity-time">09:45 AM</span>
            <span className="activity-desc">System backup completed successfully</span>
          </div>
        </div>
      </div>
    </div>
  );

  const renderUserManagement = () => (
    <div className="user-management">
      <div className="section-header">
        <h3>User Management</h3>
        <button className="btn-primary" onClick={handleAddUser}>Add New User</button>
      </div>
      
      <div className="user-table">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Username</th>
              <th>Email</th>
              <th>Role</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {Array.isArray(users) && users.map(user => (
              <tr key={user.id}>
                <td>{user.id}</td>
                <td>{user.username}</td>
                <td>{user.email}</td>
                <td>
                  <span className={`role-badge role-${user.role.toLowerCase()}`}>
                    {user.role}
                  </span>
                </td>
                <td>
                  <span className={`status-badge status-${user.status.toLowerCase()}`}>
                    {user.status}
                  </span>
                </td>
                <td>
                  <button className="btn-small" onClick={() => handleEditUser(user.id)}>Edit</button>
                  <button className="btn-small btn-danger" onClick={() => handleSuspendUser(user.id)}>Suspend</button>
                </td>
              </tr>
            )) || <tr><td colSpan="6">Loading users...</td></tr>}
          </tbody>
        </table>
      </div>
    </div>
  );

  const renderWorkflowManagement = () => (
    <div className="workflow-management">
      <div className="section-header">
        <h3>Workflow Management</h3>
        <button className="btn-primary" onClick={handleCreateWorkflow}>Create Workflow</button>
      </div>

      <div className="workflow-stats">
        <h4>Workflow Status Distribution</h4>
        <div className="workflow-chart">
          {workflows.map(([status, count]) => (
            <div key={status} className="workflow-stat">
              <span className="workflow-status">{status}</span>
              <span className="workflow-count">{count}</span>
            </div>
          ))}
        </div>
      </div>

      <div className="workflow-controls">
        <h4>Workflow Configuration</h4>
        <div className="config-grid">
          <div className="config-item">
            <label>Auto-assign Moderators</label>
            <input type="checkbox" defaultChecked />
          </div>
          <div className="config-item">
            <label>Content Review Required</label>
            <input type="checkbox" defaultChecked />
          </div>
          <div className="config-item">
            <label>Policy Compliance Check</label>
            <input type="checkbox" defaultChecked />
          </div>
        </div>
      </div>
    </div>
  );

  const renderIntegrations = () => (
    <div className="integrations-management">
      <div className="section-header">
        <h3>System Integrations</h3>
        <button className="btn-primary">Add Integration</button>
      </div>

      <div className="integrations-grid">
        {Array.isArray(integrations) && integrations.map((integration, index) => (
          <div key={index} className="integration-card">
            <div className="integration-header">
              <h4>{integration.name}</h4>
              <span className={`status-indicator ${integration.status.toLowerCase()}`}>
                {integration.status}
              </span>
            </div>
            <div className="integration-details">
              <p>Last Sync: {integration.lastSync}</p>
              <div className="integration-actions">
                <button className="btn-small">Configure</button>
                <button className="btn-small" onClick={() => handleTestIntegration(integration.name)}>Test</button>
                {integration.status === 'CONNECTED' ? (
                  <button className="btn-small btn-danger" onClick={() => handleDisconnectIntegration(integration.name)}>Disconnect</button>
                ) : (
                  <button className="btn-small btn-success" onClick={() => handleConnectIntegration(integration.name)}>Connect</button>
                )}
              </div>
            </div>
          </div>
        )) || <div className="integration-card"><p>Loading integrations...</p></div>}
      </div>

      <div className="integration-settings">
        <h4>Integration Settings</h4>
        <div className="settings-form">
          <div className="form-group">
            <label>API Rate Limit (requests/hour)</label>
            <input type="number" defaultValue="1000" />
          </div>
          <div className="form-group">
            <label>Sync Frequency (minutes)</label>
            <input type="number" defaultValue="15" />
          </div>
          <div className="form-group">
            <label>Error Retry Attempts</label>
            <input type="number" defaultValue="3" />
          </div>
        </div>
      </div>
    </div>
  );

  const renderSystemSettings = () => (
    <div className="system-settings">
      <div className="section-header">
        <h3>System Configuration</h3>
        <button className="btn-primary">Save Changes</button>
      </div>

      <div className="settings-sections">
        <div className="settings-section">
          <h4>Content Policies</h4>
          <div className="policy-controls">
            <div className="policy-item">
              <label>Minimum Content Length (words)</label>
              <input type="number" defaultValue="100" />
            </div>
            <div className="policy-item">
              <label>Auto-moderation Enabled</label>
              <input type="checkbox" defaultChecked />
            </div>
            <div className="policy-item">
              <label>Spam Detection Sensitivity</label>
              <select defaultValue="medium">
                <option value="low">Low</option>
                <option value="medium">Medium</option>
                <option value="high">High</option>
              </select>
            </div>
          </div>
        </div>

        <div className="settings-section">
          <h4>Security Settings</h4>
          <div className="security-controls">
            <div className="security-item">
              <label>JWT Token Expiry (hours)</label>
              <input type="number" defaultValue="24" />
            </div>
            <div className="security-item">
              <label>Failed Login Attempts Limit</label>
              <input type="number" defaultValue="5" />
            </div>
            <div className="security-item">
              <label>Two-Factor Authentication</label>
              <input type="checkbox" />
            </div>
          </div>
        </div>

        <div className="settings-section">
          <h4>Performance Settings</h4>
          <div className="performance-controls">
            <div className="performance-item">
              <label>Cache Duration (minutes)</label>
              <input type="number" defaultValue="60" />
            </div>
            <div className="performance-item">
              <label>Max Concurrent Users</label>
              <input type="number" defaultValue="1000" />
            </div>
            <div className="performance-item">
              <label>Database Connection Pool Size</label>
              <input type="number" defaultValue="20" />
            </div>
          </div>
        </div>
      </div>
    </div>
  );

  return (
    <div className="admin-dashboard">
      <div className="admin-header">
        <h1>System Administration</h1>
        <div className="admin-nav">
          <button 
            className={activeTab === 'overview' ? 'active' : ''}
            onClick={() => setActiveTab('overview')}
          >
            📊 Overview
          </button>
          <button 
            className={activeTab === 'users' ? 'active' : ''}
            onClick={() => setActiveTab('users')}
          >
            👥 Users
          </button>
          <button 
            className={activeTab === 'workflows' ? 'active' : ''}
            onClick={() => setActiveTab('workflows')}
          >
            ⚙️ Workflows
          </button>
          <button 
            className={activeTab === 'integrations' ? 'active' : ''}
            onClick={() => setActiveTab('integrations')}
          >
            🔗 Integrations
          </button>
          <button 
            className={activeTab === 'settings' ? 'active' : ''}
            onClick={() => setActiveTab('settings')}
          >
            🛠️ Settings
          </button>
        </div>
      </div>

      <div className="admin-content">
        {activeTab === 'overview' && renderOverview()}
        {activeTab === 'users' && renderUserManagement()}
        {activeTab === 'workflows' && renderWorkflowManagement()}
        {activeTab === 'integrations' && renderIntegrations()}
        {activeTab === 'settings' && renderSystemSettings()}
      </div>
    </div>
  );
};

export default AdminDashboard;
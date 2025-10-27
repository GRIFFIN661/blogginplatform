import React, { useState, useEffect } from 'react';
import { get, post } from '../services/api';

export default function EnhancedAdminPanel() {
  const [activeTab, setActiveTab] = useState('overview');
  const [users, setUsers] = useState([]);
  const [systemStats, setSystemStats] = useState({});
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    fetchSystemData();
  }, []);

  const fetchSystemData = async () => {
    setLoading(true);
    try {
      const [usersData, statsData] = await Promise.all([
        get('/admin/users'),
        get('/admin/stats')
      ]);
      setUsers(Array.isArray(usersData) ? usersData : []);
      setSystemStats(statsData || {});
    } catch (error) {
      console.error('Error fetching admin data:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleUserAction = async (userId, action) => {
    try {
      await post(`/admin/users/${userId}/${action}`, {});
      fetchSystemData();
    } catch (error) {
      console.error('Error performing user action:', error);
    }
  };

  const handleSystemConfig = async (config) => {
    try {
      await post('/admin/config', config);
      alert('Configuration updated successfully');
    } catch (error) {
      console.error('Error updating config:', error);
    }
  };

  return (
    <div className="enhanced-admin-panel">
      <div className="admin-header">
        <h1>System Administration</h1>
        <div className="admin-nav">
          <button 
            className={activeTab === 'overview' ? 'active' : ''}
            onClick={() => setActiveTab('overview')}
          >
            Overview
          </button>
          <button 
            className={activeTab === 'users' ? 'active' : ''}
            onClick={() => setActiveTab('users')}
          >
            User Management
          </button>
          <button 
            className={activeTab === 'content' ? 'active' : ''}
            onClick={() => setActiveTab('content')}
          >
            Content Oversight
          </button>
          <button 
            className={activeTab === 'system' ? 'active' : ''}
            onClick={() => setActiveTab('system')}
          >
            System Config
          </button>
        </div>
      </div>

      <div className="admin-content">
        {activeTab === 'overview' && (
          <div className="admin-overview">
            <div className="stats-grid">
              <div className="stat-card">
                <h3>Total Users</h3>
                <div className="stat-value">{systemStats.totalUsers || 0}</div>
                <div className="stat-change">+12% this month</div>
              </div>
              <div className="stat-card">
                <h3>Active Sessions</h3>
                <div className="stat-value">{systemStats.activeSessions || 0}</div>
                <div className="stat-change">Currently online</div>
              </div>
              <div className="stat-card">
                <h3>Total Posts</h3>
                <div className="stat-value">{systemStats.totalPosts || 0}</div>
                <div className="stat-change">+8% this week</div>
              </div>
              <div className="stat-card">
                <h3>System Health</h3>
                <div className="stat-value health-good">98%</div>
                <div className="stat-change">All systems operational</div>
              </div>
            </div>

            <div className="recent-activity">
              <h3>Recent System Activity</h3>
              <div className="activity-list">
                <div className="activity-item">
                  <span className="activity-time">10:30 AM</span>
                  <span className="activity-desc">New user registration: john.doe@email.com</span>
                </div>
                <div className="activity-item">
                  <span className="activity-time">10:15 AM</span>
                  <span className="activity-desc">Blog post published: "Getting Started with React"</span>
                </div>
                <div className="activity-item">
                  <span className="activity-time">09:45 AM</span>
                  <span className="activity-desc">System backup completed successfully</span>
                </div>
                <div className="activity-item">
                  <span className="activity-time">09:30 AM</span>
                  <span className="activity-desc">Comment moderation: 5 comments approved</span>
                </div>
              </div>
            </div>
          </div>
        )}

        {activeTab === 'users' && (
          <div className="user-management">
            <div className="section-header">
              <h3>User Management</h3>
              <button className="btn-primary">Add New User</button>
            </div>
            
            {loading ? (
              <div className="loading">Loading users...</div>
            ) : (
              <div className="user-table">
                <table>
                  <thead>
                    <tr>
                      <th>User</th>
                      <th>Email</th>
                      <th>Role</th>
                      <th>Status</th>
                      <th>Last Login</th>
                      <th>Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {users && users.length > 0 ? users.map(user => (
                      <tr key={user.id}>
                        <td>{user.username}</td>
                        <td>{user.email}</td>
                        <td>
                          <span className={`role-badge role-${user.role.toLowerCase().replace('role_', '')}`}>
                            {user.role.replace('ROLE_', '')}
                          </span>
                        </td>
                        <td>
                          <span className={`status-badge status-${user.isActive ? 'active' : 'inactive'}`}>
                            {user.isActive ? 'Active' : 'Inactive'}
                          </span>
                        </td>
                        <td>{user.lastLogin ? new Date(user.lastLogin).toLocaleDateString() : 'Never'}</td>
                        <td>
                          <button 
                            className="btn-small"
                            onClick={() => handleUserAction(user.id, 'edit')}
                          >
                            Edit
                          </button>
                          <button 
                            className={`btn-small ${user.isActive ? 'btn-danger' : 'btn-success'}`}
                            onClick={() => handleUserAction(user.id, user.isActive ? 'deactivate' : 'activate')}
                          >
                            {user.isActive ? 'Deactivate' : 'Activate'}
                          </button>
                          <button 
                            className="btn-small btn-danger"
                            onClick={() => handleUserAction(user.id, 'delete')}
                          >
                            Delete
                          </button>
                        </td>
                      </tr>
                    )) : (
                      <tr>
                        <td colSpan="6" style={{textAlign: 'center', padding: '2rem'}}>No users found</td>
                      </tr>
                    )}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        )}

        {activeTab === 'content' && (
          <div className="content-oversight">
            <h3>Content Moderation</h3>
            <div className="moderation-stats">
              <div className="mod-stat">
                <h4>Pending Reviews</h4>
                <span className="mod-count">23</span>
              </div>
              <div className="mod-stat">
                <h4>Flagged Content</h4>
                <span className="mod-count">7</span>
              </div>
              <div className="mod-stat">
                <h4>Reports</h4>
                <span className="mod-count">12</span>
              </div>
            </div>
            
            <div className="content-actions">
              <button className="btn-primary">Review Pending Content</button>
              <button className="btn-primary">Manage Reports</button>
              <button className="btn-primary">Content Analytics</button>
            </div>
          </div>
        )}

        {activeTab === 'system' && (
          <div className="system-settings">
            <h3>System Configuration</h3>
            <div className="settings-sections">
              <div className="settings-section">
                <h4>Content Policies</h4>
                <div className="policy-controls">
                  <div className="policy-item">
                    <label>Auto-approve comments</label>
                    <input type="checkbox" defaultChecked />
                  </div>
                  <div className="policy-item">
                    <label>Require email verification</label>
                    <input type="checkbox" defaultChecked />
                  </div>
                  <div className="policy-item">
                    <label>Max post length</label>
                    <input type="number" defaultValue="10000" />
                  </div>
                </div>
              </div>

              <div className="settings-section">
                <h4>Security Settings</h4>
                <div className="security-controls">
                  <div className="security-item">
                    <label>Enable 2FA</label>
                    <input type="checkbox" />
                  </div>
                  <div className="security-item">
                    <label>Session timeout (minutes)</label>
                    <input type="number" defaultValue="30" />
                  </div>
                  <div className="security-item">
                    <label>Password strength</label>
                    <select defaultValue="medium">
                      <option value="low">Low</option>
                      <option value="medium">Medium</option>
                      <option value="high">High</option>
                    </select>
                  </div>
                </div>
              </div>

              <div className="settings-section">
                <h4>Performance</h4>
                <div className="performance-controls">
                  <div className="performance-item">
                    <label>Cache duration (hours)</label>
                    <input type="number" defaultValue="24" />
                  </div>
                  <div className="performance-item">
                    <label>Max concurrent users</label>
                    <input type="number" defaultValue="1000" />
                  </div>
                  <div className="performance-item">
                    <label>Enable compression</label>
                    <input type="checkbox" defaultChecked />
                  </div>
                </div>
              </div>
            </div>
            
            <div className="settings-actions">
              <button 
                className="btn-primary"
                onClick={() => handleSystemConfig({})}
              >
                Save Configuration
              </button>
              <button className="btn-primary">Backup System</button>
              <button className="btn-primary">View Logs</button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
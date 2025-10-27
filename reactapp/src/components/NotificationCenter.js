import React, { useState, useEffect } from 'react';

const NotificationCenter = ({ userId }) => {
  const [notifications, setNotifications] = useState([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const [showNotifications, setShowNotifications] = useState(false);
  const [notificationPreferences, setNotificationPreferences] = useState({
    email: true,
    inApp: true,
    push: false,
    content: true,
    community: true,
    platform: true
  });

  useEffect(() => {
    if (userId) {
      fetchNotifications();
      fetchUnreadCount();
      
      // Set up real-time notification polling
      const interval = setInterval(fetchUnreadCount, 30000);
      return () => clearInterval(interval);
    }
  }, [userId]);

  const fetchNotifications = async () => {
    try {
      const response = await fetch(`/api/notifications/user/${userId}`);
      const data = await response.json();
      setNotifications(Array.isArray(data) ? data : []);
    } catch (error) {
      console.error('Error fetching notifications:', error);
      setNotifications([]);
    }
  };

  const fetchUnreadCount = async () => {
    try {
      const response = await fetch(`/api/notifications/user/${userId}/unread`);
      const data = await response.json();
      setUnreadCount(Array.isArray(data) ? data.length : 0);
    } catch (error) {
      console.error('Error fetching unread count:', error);
      setUnreadCount(0);
    }
  };

  const markAsRead = async (notificationId) => {
    try {
      await fetch(`/api/notifications/${notificationId}/read`, {
        method: 'PUT'
      });
      
      // Update local state
      setNotifications(prev => 
        prev.map(n => 
          n.id === notificationId ? { ...n, isRead: true } : n
        )
      );
      setUnreadCount(prev => Math.max(0, prev - 1));
    } catch (error) {
      console.error('Error marking notification as read:', error);
    }
  };

  const updatePreferences = async (newPreferences) => {
    try {
      await fetch(`/api/notifications/preferences/${userId}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(newPreferences)
      });
      setNotificationPreferences(newPreferences);
    } catch (error) {
      console.error('Error updating preferences:', error);
    }
  };

  const getPriorityColor = (priority) => {
    switch (priority) {
      case 'URGENT': return '#ff4444';
      case 'HIGH': return '#ff8800';
      case 'MEDIUM': return '#ffaa00';
      case 'LOW': return '#00aa00';
      default: return '#666666';
    }
  };

  const getCategoryIcon = (category) => {
    switch (category) {
      case 'CONTENT': return '📝';
      case 'COMMUNITY': return '👥';
      case 'PLATFORM': return '🔧';
      case 'EMERGENCY': return '🚨';
      default: return '📢';
    }
  };

  const formatTimeAgo = (timestamp) => {
    const now = new Date();
    const time = new Date(timestamp);
    const diffInMinutes = Math.floor((now - time) / (1000 * 60));
    
    if (diffInMinutes < 1) return 'Just now';
    if (diffInMinutes < 60) return `${diffInMinutes}m ago`;
    if (diffInMinutes < 1440) return `${Math.floor(diffInMinutes / 60)}h ago`;
    return `${Math.floor(diffInMinutes / 1440)}d ago`;
  };

  return (
    <div className="notification-center">
      {/* Notification Bell */}
      <div 
        className="notification-bell"
        onClick={() => setShowNotifications(!showNotifications)}
      >
        <span className="bell-icon">🔔</span>
        {unreadCount > 0 && (
          <span className="notification-badge">{unreadCount}</span>
        )}
      </div>

      {/* Notification Dropdown */}
      {showNotifications && (
        <div className="notification-dropdown">
          <div className="notification-header">
            <h3>Notifications</h3>
            <button 
              className="preferences-btn"
              onClick={() => setShowNotifications(false)}
            >
              ⚙️
            </button>
          </div>

          <div className="notification-list">
            {notifications.length === 0 ? (
              <div className="no-notifications">No notifications</div>
            ) : (
              notifications.map(notification => (
                <div 
                  key={notification.id}
                  className={`notification-item ${!notification.isRead ? 'unread' : ''}`}
                  onClick={() => !notification.isRead && markAsRead(notification.id)}
                >
                  <div className="notification-content">
                    <div className="notification-header-item">
                      <span className="category-icon">
                        {getCategoryIcon(notification.category)}
                      </span>
                      <span className="notification-title">{notification.title}</span>
                      <span 
                        className="priority-indicator"
                        style={{ backgroundColor: getPriorityColor(notification.priority) }}
                      ></span>
                    </div>
                    <div className="notification-message">
                      {notification.message}
                    </div>
                    <div className="notification-meta">
                      <span className="notification-time">
                        {formatTimeAgo(notification.createdAt)}
                      </span>
                      <span className="notification-type">
                        {notification.type}
                      </span>
                    </div>
                  </div>
                </div>
              ))
            )}
          </div>

          {/* Notification Preferences */}
          <div className="notification-preferences">
            <h4>Notification Preferences</h4>
            <div className="preference-controls">
              <label>
                <input
                  type="checkbox"
                  checked={notificationPreferences.email}
                  onChange={(e) => updatePreferences({
                    ...notificationPreferences,
                    email: e.target.checked
                  })}
                />
                Email Notifications
              </label>
              <label>
                <input
                  type="checkbox"
                  checked={notificationPreferences.push}
                  onChange={(e) => updatePreferences({
                    ...notificationPreferences,
                    push: e.target.checked
                  })}
                />
                Push Notifications
              </label>
              <label>
                <input
                  type="checkbox"
                  checked={notificationPreferences.content}
                  onChange={(e) => updatePreferences({
                    ...notificationPreferences,
                    content: e.target.checked
                  })}
                />
                Content Updates
              </label>
              <label>
                <input
                  type="checkbox"
                  checked={notificationPreferences.community}
                  onChange={(e) => updatePreferences({
                    ...notificationPreferences,
                    community: e.target.checked
                  })}
                />
                Community Alerts
              </label>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default NotificationCenter;
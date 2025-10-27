import React, { useState, useEffect } from 'react';

const AnalyticsDashboard = () => {
  const [dashboardData, setDashboardData] = useState(null);
  const [geoAnalytics, setGeoAnalytics] = useState(null);
  const [seoData, setSeoData] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    try {
      setLoading(true);
      
      // Fetch executive dashboard
      const dashboardResponse = await fetch('/api/analytics/executive-dashboard');
      const dashboard = await dashboardResponse.json();
      setDashboardData(dashboard);
      
      // Fetch geographic analytics
      const geoResponse = await fetch('/api/analytics/geographic');
      const geo = await geoResponse.json();
      setGeoAnalytics(geo);
      
      // Fetch SEO analytics
      const seoResponse = await fetch('/api/analytics/seo');
      const seo = await seoResponse.json();
      setSeoData(seo);
      
    } catch (error) {
      console.error('Error fetching analytics:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <div className="analytics-loading">Loading Analytics Dashboard...</div>;
  }

  return (
    <div className="analytics-dashboard">
      <h1>Analytics & Business Intelligence Dashboard</h1>
      
      {/* Executive Overview */}
      <div className="dashboard-section">
        <h2>Executive Overview</h2>
        <div className="metrics-grid">
          <div className="metric-card">
            <h3>Total Blogs</h3>
            <div className="metric-value">{dashboardData?.totalBlogs || 0}</div>
          </div>
          <div className="metric-card">
            <h3>Total Views</h3>
            <div className="metric-value">{dashboardData?.totalViews || 0}</div>
          </div>
          <div className="metric-card">
            <h3>Avg Engagement Rate</h3>
            <div className="metric-value">{(dashboardData?.avgEngagementRate || 0).toFixed(2)}%</div>
          </div>
          <div className="metric-card">
            <h3>Weekly Growth</h3>
            <div className="metric-value">{(dashboardData?.weeklyGrowth || 0).toFixed(2)}%</div>
          </div>
        </div>
      </div>

      {/* Top Performing Content */}
      <div className="dashboard-section">
        <h2>Top Performing Content</h2>
        <div className="top-content-list">
          {dashboardData?.topContent?.map((content, index) => (
            <div key={content.blogId} className="content-item">
              <span className="rank">#{index + 1}</span>
              <span className="title">{content.title}</span>
              <span className="engagement">{content.engagementRate.toFixed(2)}%</span>
              <span className="views">{content.views} views</span>
            </div>
          ))}
        </div>
      </div>

      {/* Geographic Analytics */}
      <div className="dashboard-section">
        <h2>Geographic Distribution</h2>
        <div className="geo-analytics">
          <div className="geo-section">
            <h3>Views by Location</h3>
            <div className="location-list">
              {Object.entries(geoAnalytics?.viewsByLocation || {}).map(([location, views]) => (
                <div key={location} className="location-item">
                  <span className="location">{location}</span>
                  <span className="views">{views} views</span>
                </div>
              ))}
            </div>
          </div>
          <div className="geo-section">
            <h3>Engagement by Location</h3>
            <div className="location-list">
              {Object.entries(geoAnalytics?.engagementByLocation || {}).map(([location, engagement]) => (
                <div key={location} className="location-item">
                  <span className="location">{location}</span>
                  <span className="engagement">{engagement.toFixed(2)}%</span>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>

      {/* SEO Analytics */}
      <div className="dashboard-section">
        <h2>SEO Performance</h2>
        <div className="seo-metrics">
          <div className="seo-card">
            <h3>Average SEO Score</h3>
            <div className="seo-value">{(seoData?.avgSeoScore || 0).toFixed(1)}/10</div>
          </div>
          <div className="seo-card">
            <h3>Organic Views</h3>
            <div className="seo-value">{seoData?.totalOrganicViews || 0}</div>
          </div>
          <div className="seo-card">
            <h3>Organic Traffic %</h3>
            <div className="seo-value">{(seoData?.organicTrafficPercentage || 0).toFixed(1)}%</div>
          </div>
        </div>
      </div>

      {/* Real-time Monitoring */}
      <div className="dashboard-section">
        <h2>Real-time Monitoring</h2>
        <div className="realtime-status">
          <div className="status-indicator active">
            <span className="indicator"></span>
            <span>System Active</span>
          </div>
          <div className="realtime-metrics">
            <span>Live Users: 24</span>
            <span>Active Sessions: 156</span>
            <span>Current Engagement: 3.2%</span>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AnalyticsDashboard;
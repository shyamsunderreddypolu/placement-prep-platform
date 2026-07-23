import React, { useState, useEffect } from 'react';
import Navbar from '../components/Navbar';
import { getDifficultyStats, getTopicStats, getStreakStats } from '../services/dashboardService';
import { Flame, Trophy, Award, Layers, HelpCircle } from 'lucide-react';
import {
  ResponsiveContainer,
  PieChart,
  Pie,
  Cell,
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  Legend
} from 'recharts';

const Dashboard = () => {
  const [difficultyData, setDifficultyData] = useState([]);
  const [topicData, setTopicData] = useState([]);
  const [streak, setStreak] = useState({ currentStreak: 0, longestStreak: 0 });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const loadDashboardData = async () => {
    setLoading(true);
    setError('');
    try {
      const [diffRes, topicRes, streakRes] = await Promise.all([
        getDifficultyStats(),
        getTopicStats(),
        getStreakStats()
      ]);

      // Format difficulty stats for Recharts Pie Chart
      const formattedDiff = Object.keys(diffRes).map((key) => ({
        name: key,
        value: diffRes[key]
      }));
      setDifficultyData(formattedDiff);

      // Format topic stats for Recharts Bar Chart
      const formattedTopic = Object.keys(topicRes).map((key) => ({
        topic: key,
        solved: topicRes[key]
      }));
      setTopicData(formattedTopic);

      setStreak(streakRes);
    } catch (err) {
      setError('Failed to retrieve dashboard analytics. Verify backend connection.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadDashboardData();
  }, []);

  const totalSolved = difficultyData.reduce((sum, item) => sum + item.value, 0);

  // Custom styling colors for Recharts difficulty levels
  const COLORS = {
    EASY: '#10b981',   // Emerald Green
    MEDIUM: '#f59e0b', // Amber/Yellow
    HARD: '#ef4444'    // Red
  };

  return (
    <>
      <Navbar />
      <div className="page-container">
        <div className="page-header" style={{ marginBottom: '1.5rem' }}>
          <div>
            <h1 className="page-title">Placement Prep Dashboard</h1>
            <p className="page-subtitle">Track your DSA statistics, streak progress, and interview readiness</p>
          </div>
        </div>

        {error && <div className="error-banner">{error}</div>}

        {loading ? (
          <div style={{ textAlign: 'center', padding: '4rem', color: 'var(--text-muted)' }}>
            Loading dashboard analytics...
          </div>
        ) : (
          <>
            {/* Quick Metrics Grid */}
            <div className="metrics-grid" style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: '1.5rem', marginBottom: '2rem' }}>
              
              {/* Total Solved Card */}
              <div className="metric-card" style={{ background: 'var(--bg-card)', padding: '1.5rem', borderRadius: '0.75rem', border: '1px solid var(--border-color)', display: 'flex', alignItems: 'center', gap: '1rem', backdropFilter: 'blur(12px)' }}>
                <div style={{ background: 'rgba(99, 102, 241, 0.15)', padding: '0.75rem', borderRadius: '0.5rem', color: 'var(--primary-accent)' }}>
                  <Award size={28} />
                </div>
                <div>
                  <div style={{ color: 'var(--text-muted)', fontSize: '0.85rem', fontWeight: 500 }}>Total Solved</div>
                  <div style={{ fontSize: '1.5rem', fontWeight: 700, marginTop: '0.25rem' }}>{totalSolved}</div>
                </div>
              </div>

              {/* Current Streak Card */}
              <div className="metric-card" style={{ background: 'var(--bg-card)', padding: '1.5rem', borderRadius: '0.75rem', border: '1px solid var(--border-color)', display: 'flex', alignItems: 'center', gap: '1rem', backdropFilter: 'blur(12px)' }}>
                <div style={{ background: 'rgba(239, 68, 68, 0.15)', padding: '0.75rem', borderRadius: '0.5rem', color: 'var(--danger-color)' }}>
                  <Flame size={28} />
                </div>
                <div>
                  <div style={{ color: 'var(--text-muted)', fontSize: '0.85rem', fontWeight: 500 }}>Current Streak</div>
                  <div style={{ fontSize: '1.5rem', fontWeight: 700, marginTop: '0.25rem' }}>{streak.currentStreak} Days</div>
                </div>
              </div>

              {/* Longest Streak Card */}
              <div className="metric-card" style={{ background: 'var(--bg-card)', padding: '1.5rem', borderRadius: '0.75rem', border: '1px solid var(--border-color)', display: 'flex', alignItems: 'center', gap: '1rem', backdropFilter: 'blur(12px)' }}>
                <div style={{ background: 'rgba(245, 158, 11, 0.15)', padding: '0.75rem', borderRadius: '0.5rem', color: 'var(--warning-color)' }}>
                  <Trophy size={28} />
                </div>
                <div>
                  <div style={{ color: 'var(--text-muted)', fontSize: '0.85rem', fontWeight: 500 }}>Longest Streak</div>
                  <div style={{ fontSize: '1.5rem', fontWeight: 700, marginTop: '0.25rem' }}>{streak.longestStreak} Days</div>
                </div>
              </div>
            </div>

            {/* Charts Section */}
            <div className="charts-grid" style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(400px, 1fr))', gap: '2rem' }}>
              
              {/* Difficulty breakdown chart card */}
              <div className="chart-card" style={{ background: 'var(--bg-card)', padding: '2rem', borderRadius: '1rem', border: '1px solid var(--border-color)', backdropFilter: 'blur(12px)' }}>
                <h3 style={{ fontSize: '1.1rem', fontWeight: 600, marginBottom: '1.5rem' }}>Solved Problems by Difficulty</h3>
                
                {totalSolved === 0 ? (
                  <div style={{ height: '240px', display: 'flex', alignItems: 'center', justifyContent: 'center', color: 'var(--text-muted)' }}>
                    No problems solved yet. Go to problems list and submit a solution!
                  </div>
                ) : (
                  <div style={{ display: 'flex', flexWrap: 'wrap', alignItems: 'center', gap: '1rem' }}>
                    <div style={{ flex: 1, minWidth: '180px', height: '240px' }}>
                      <ResponsiveContainer width="100%" height="100%">
                        <PieChart>
                          <Pie
                            data={difficultyData}
                            innerRadius={60}
                            outerRadius={80}
                            paddingAngle={5}
                            dataKey="value"
                          >
                            {difficultyData.map((entry, index) => (
                              <Cell key={`cell-${index}`} fill={COLORS[entry.name] || '#6366f1'} />
                            ))}
                          </Pie>
                          <Tooltip contentStyle={{ background: '#1e293b', border: '1px solid rgba(255,255,255,0.1)', borderRadius: '8px', color: '#fff' }} />
                        </PieChart>
                      </ResponsiveContainer>
                    </div>

                    <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem', minWidth: '120px' }}>
                      {difficultyData.map((item) => (
                        <div key={item.name} style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', fontSize: '0.9rem' }}>
                          <span style={{ width: '12px', height: '12px', borderRadius: '50%', background: COLORS[item.name] }}></span>
                          <span style={{ textTransform: 'capitalize', fontWeight: 500 }}>{item.name.toLowerCase()}</span>
                          <span style={{ color: 'var(--text-muted)' }}>({item.value})</span>
                        </div>
                      ))}
                    </div>
                  </div>
                )}
              </div>

              {/* Topic coverage chart card */}
              <div className="chart-card" style={{ background: 'var(--bg-card)', padding: '2rem', borderRadius: '1rem', border: '1px solid var(--border-color)', backdropFilter: 'blur(12px)' }}>
                <h3 style={{ fontSize: '1.1rem', fontWeight: 600, marginBottom: '1.5rem' }}>Topic-wise Analytics</h3>

                {topicData.length === 0 ? (
                  <div style={{ height: '240px', display: 'flex', alignItems: 'center', justifyContent: 'center', color: 'var(--text-muted)' }}>
                    No topic stats available. Submit solutions to populate details.
                  </div>
                ) : (
                  <div style={{ width: '100%', height: '240px' }}>
                    <ResponsiveContainer width="100%" height="100%">
                      <BarChart data={topicData}>
                        <XAxis dataKey="topic" stroke="var(--text-muted)" fontSize={12} tickLine={false} />
                        <YAxis stroke="var(--text-muted)" fontSize={12} tickLine={false} allowDecimals={false} />
                        <Tooltip contentStyle={{ background: '#1e293b', border: '1px solid rgba(255,255,255,0.1)', borderRadius: '8px', color: '#fff' }} />
                        <Bar dataKey="solved" fill="url(#colorSolved)" radius={[4, 4, 0, 0]}>
                          {topicData.map((entry, index) => (
                            <Cell key={`cell-${index}`} />
                          ))}
                        </Bar>
                        <defs>
                          <linearGradient id="colorSolved" x1="0" y1="0" x2="0" y2="1">
                            <stop offset="5%" stopColor="#6366f1" stopOpacity={0.8}/>
                            <stop offset="95%" stopColor="#8b5cf6" stopOpacity={0.8}/>
                          </linearGradient>
                        </defs>
                      </BarChart>
                    </ResponsiveContainer>
                  </div>
                )}
              </div>

            </div>
          </>
        )}
      </div>
    </>
  );
};

export default Dashboard;

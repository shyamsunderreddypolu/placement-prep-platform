import React, { useState, useEffect } from 'react';
import Navbar from '../components/Navbar';
import { getDifficultyStats, getTopicStats, getStreakStats } from '../services/dashboardService';
import { getPlacementReadiness } from '../services/readinessService';
import { getDueRevisions, submitReview } from '../services/revisionService';
import { Flame, Trophy, Award, Target, CheckCircle2, AlertTriangle, Lightbulb, Clock, Check, RefreshCw } from 'lucide-react';
import {
  ResponsiveContainer,
  PieChart,
  Pie,
  Cell,
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip
} from 'recharts';

const Dashboard = () => {
  const [difficultyData, setDifficultyData] = useState([]);
  const [topicData, setTopicData] = useState([]);
  const [streak, setStreak] = useState({ currentStreak: 0, longestStreak: 0 });
  const [readiness, setReadiness] = useState(null);
  const [dueRevisions, setDueRevisions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [reviewingId, setReviewingId] = useState(null);
  const [error, setError] = useState('');

  const loadDashboardData = async () => {
    setLoading(true);
    setError('');
    try {
      const [diffRes, topicRes, streakRes, readinessRes, revisionsRes] = await Promise.all([
        getDifficultyStats(),
        getTopicStats(),
        getStreakStats(),
        getPlacementReadiness().catch(() => null),
        getDueRevisions().catch(() => [])
      ]);

      const formattedDiff = Object.keys(diffRes).map((key) => ({
        name: key,
        value: diffRes[key]
      }));
      setDifficultyData(formattedDiff);

      const formattedTopic = Object.keys(topicRes).map((key) => ({
        topic: key,
        solved: topicRes[key]
      }));
      setTopicData(formattedTopic);

      setStreak(streakRes);
      setReadiness(readinessRes);
      setDueRevisions(revisionsRes);
    } catch (err) {
      setError('Failed to retrieve dashboard analytics. Verify backend connection.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadDashboardData();
  }, []);

  const handleReview = async (submissionId, feedback) => {
    setReviewingId(submissionId);
    try {
      await submitReview(submissionId, feedback);
      // Remove reviewed item from local due list
      setDueRevisions((prev) => prev.filter((item) => item.submissionId !== submissionId));
      // Refresh readiness in background
      getPlacementReadiness().then((res) => setReadiness(res)).catch(() => {});
    } catch (err) {
      alert('Failed to log revision feedback. Please try again.');
    } finally {
      setReviewingId(null);
    }
  };

  const totalSolved = difficultyData.reduce((sum, item) => sum + item.value, 0);

  const COLORS = {
    EASY: '#10b981',
    MEDIUM: '#f59e0b',
    HARD: '#ef4444'
  };

  const getReadinessColor = (score) => {
    if (score >= 75) return '#10b981';
    if (score >= 50) return '#f59e0b';
    return '#ef4444';
  };

  return (
    <>
      <Navbar />
      <div className="container dashboard-container" style={{ paddingBottom: '3rem' }}>
        <div className="dashboard-header" style={{ marginBottom: '2rem' }}>
          <div>
            <h1 className="page-title">Placement Readiness Dashboard</h1>
            <p className="page-subtitle">Track your DSA metrics, 1-4-7 revision retention, and interview readiness</p>
          </div>
        </div>

        {error && <div className="error-banner">{error}</div>}

        {loading ? (
          <div className="spinner-container">
            <div className="spinner"></div>
            <span>Loading placement readiness metrics...</span>
          </div>
        ) : (
          <>
            {/* Placement Readiness Score Banner */}
            {readiness && (
              <div
                className="readiness-banner"
                style={{
                  background: 'linear-gradient(135deg, rgba(30, 41, 59, 0.9) 0%, rgba(15, 23, 42, 0.95) 100%)',
                  border: `1px solid ${getReadinessColor(readiness.overallScore)}40`,
                  borderRadius: '1rem',
                  padding: '1.75rem',
                  marginBottom: '2rem',
                  boxShadow: '0 8px 32px rgba(0, 0, 0, 0.25)',
                  backdropFilter: 'blur(16px)'
                }}
              >
                <div style={{ display: 'flex', flexWrap: 'wrap', alignItems: 'center', justifyContent: 'space-between', gap: '1.5rem', marginBottom: '1.25rem' }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '1.25rem' }}>
                    <div
                      style={{
                        width: '72px',
                        height: '72px',
                        borderRadius: '50%',
                        border: `4px solid ${getReadinessColor(readiness.overallScore)}`,
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        fontSize: '1.65rem',
                        fontWeight: 800,
                        color: getReadinessColor(readiness.overallScore),
                        background: 'rgba(15, 23, 42, 0.8)'
                      }}
                    >
                      {readiness.overallScore}%
                    </div>
                    <div>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                        <Target size={20} style={{ color: 'var(--primary-accent)' }} />
                        <h2 style={{ fontSize: '1.25rem', fontWeight: 700, margin: 0 }}>Placement Readiness Index</h2>
                      </div>
                      <p style={{ color: 'var(--text-muted)', fontSize: '0.85rem', margin: '0.25rem 0 0 0' }}>
                        Deterministic evaluation across DSA benchmark, Resume readiness, and Consistency
                      </p>
                    </div>
                  </div>

                  {/* Category Breakdown Chips */}
                  {readiness.categoryScores && (
                    <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.75rem' }}>
                      {Object.entries(readiness.categoryScores).map(([cat, score]) => (
                        <div
                          key={cat}
                          style={{
                            background: 'rgba(15, 23, 42, 0.7)',
                            border: '1px solid rgba(255, 255, 255, 0.08)',
                            padding: '0.5rem 0.85rem',
                            borderRadius: '0.5rem',
                            textAlign: 'center'
                          }}
                        >
                          <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', textTransform: 'uppercase', letterSpacing: '0.5px' }}>{cat}</div>
                          <div style={{ fontSize: '1.05rem', fontWeight: 700, color: getReadinessColor(score) }}>{score}%</div>
                        </div>
                      ))}
                    </div>
                  )}
                </div>

                {/* Insights & Recommendations Grid */}
                <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))', gap: '1rem', paddingTop: '1rem', borderTop: '1px solid rgba(255,255,255,0.06)' }}>
                  {/* Strengths */}
                  <div>
                    <h4 style={{ fontSize: '0.85rem', color: '#34d399', fontWeight: 600, display: 'flex', alignItems: 'center', gap: '0.4rem', marginBottom: '0.5rem' }}>
                      <CheckCircle2 size={15} /> Validated Strengths
                    </h4>
                    <ul style={{ paddingLeft: '1.1rem', margin: 0, fontSize: '0.85rem', color: '#cbd5e1', display: 'flex', flexDirection: 'column', gap: '0.25rem' }}>
                      {readiness.strengths.length > 0 ? (
                        readiness.strengths.map((s, idx) => <li key={idx}>{s}</li>)
                      ) : (
                        <li style={{ color: 'var(--text-muted)' }}>Solve problems to unlock strength insights</li>
                      )}
                    </ul>
                  </div>

                  {/* Weaknesses */}
                  <div>
                    <h4 style={{ fontSize: '0.85rem', color: '#f87171', fontWeight: 600, display: 'flex', alignItems: 'center', gap: '0.4rem', marginBottom: '0.5rem' }}>
                      <AlertTriangle size={15} /> Target Improvement Areas
                    </h4>
                    <ul style={{ paddingLeft: '1.1rem', margin: 0, fontSize: '0.85rem', color: '#cbd5e1', display: 'flex', flexDirection: 'column', gap: '0.25rem' }}>
                      {readiness.weaknesses.map((w, idx) => <li key={idx}>{w}</li>)}
                    </ul>
                  </div>

                  {/* Recommendations */}
                  <div>
                    <h4 style={{ fontSize: '0.85rem', color: 'var(--warning-color)', fontWeight: 600, display: 'flex', alignItems: 'center', gap: '0.4rem', marginBottom: '0.5rem' }}>
                      <Lightbulb size={15} /> Priority Actions
                    </h4>
                    <ul style={{ paddingLeft: '1.1rem', margin: 0, fontSize: '0.85rem', color: '#cbd5e1', display: 'flex', flexDirection: 'column', gap: '0.25rem' }}>
                      {readiness.recommendations.map((r, idx) => <li key={idx}>{r}</li>)}
                    </ul>
                  </div>
                </div>
              </div>
            )}

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

              {/* Due Revisions Count Card */}
              <div className="metric-card" style={{ background: 'var(--bg-card)', padding: '1.5rem', borderRadius: '0.75rem', border: '1px solid var(--border-color)', display: 'flex', alignItems: 'center', gap: '1rem', backdropFilter: 'blur(12px)' }}>
                <div style={{ background: 'rgba(14, 165, 233, 0.15)', padding: '0.75rem', borderRadius: '0.5rem', color: '#38bdf8' }}>
                  <Clock size={28} />
                </div>
                <div>
                  <div style={{ color: 'var(--text-muted)', fontSize: '0.85rem', fontWeight: 500 }}>1-4-7 Revisions Due</div>
                  <div style={{ fontSize: '1.5rem', fontWeight: 700, marginTop: '0.25rem' }}>{dueRevisions.length}</div>
                </div>
              </div>
            </div>

            {/* 1-4-7 Revision Due Queue Widget */}
            <div style={{ background: 'var(--bg-card)', padding: '1.5rem', borderRadius: '0.75rem', border: '1px solid var(--border-color)', marginBottom: '2rem', backdropFilter: 'blur(12px)' }}>
              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '1rem' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                  <RefreshCw size={18} style={{ color: 'var(--primary-accent)' }} />
                  <h3 style={{ fontSize: '1.1rem', fontWeight: 600, margin: 0 }}>1-4-7 Spaced Repetition Due Queue</h3>
                </div>
                <span style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>Day 1 ? Day 4 ? Day 7 Retention System</span>
              </div>

              {dueRevisions.length === 0 ? (
                <div style={{ padding: '1rem', textAlign: 'center', color: '#94a3b8', fontSize: '0.9rem' }}>
                  <Check size={24} style={{ color: '#10b981', display: 'block', margin: '0 auto 0.5rem auto' }} />
                  All spaced repetition reviews are up to date! Great consistency.
                </div>
              ) : (
                <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
                  {dueRevisions.map((rev) => (
                    <div
                      key={rev.submissionId}
                      style={{
                        display: 'flex',
                        flexWrap: 'wrap',
                        alignItems: 'center',
                        justifyContent: 'space-between',
                        padding: '0.85rem 1rem',
                        background: 'rgba(15, 23, 42, 0.6)',
                        borderRadius: '0.5rem',
                        border: '1px solid rgba(255,255,255,0.05)',
                        gap: '0.75rem'
                      }}
                    >
                      <div>
                        <div style={{ fontWeight: 600, fontSize: '0.95rem' }}>{rev.problemTitle}</div>
                        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginTop: '0.2rem', fontSize: '0.8rem', color: 'var(--text-muted)' }}>
                          <span style={{ textTransform: 'capitalize', color: COLORS[rev.difficulty] }}>{rev.difficulty.toLowerCase()}</span>
                          <span>•</span>
                          <span>{rev.topic}</span>
                          <span>•</span>
                          <span>Reviewed {rev.reviewCount} time(s)</span>
                        </div>
                      </div>

                      <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                        <button
                          className="btn-primary"
                          disabled={reviewingId === rev.submissionId}
                          onClick={() => handleReview(rev.submissionId, 'EASY')}
                          style={{ padding: '0.35rem 0.75rem', fontSize: '0.8rem', background: '#059669', borderColor: '#059669' }}
                        >
                          Easy (+7d)
                        </button>
                        <button
                          className="btn-primary"
                          disabled={reviewingId === rev.submissionId}
                          onClick={() => handleReview(rev.submissionId, 'NEEDS_REVIEW')}
                          style={{ padding: '0.35rem 0.75rem', fontSize: '0.8rem', background: '#d97706', borderColor: '#d97706' }}
                        >
                          Review (+3d)
                        </button>
                        <button
                          className="btn-primary"
                          disabled={reviewingId === rev.submissionId}
                          onClick={() => handleReview(rev.submissionId, 'HARD')}
                          style={{ padding: '0.35rem 0.75rem', fontSize: '0.8rem', background: '#dc2626', borderColor: '#dc2626' }}
                        >
                          Hard (+1d)
                        </button>
                      </div>
                    </div>
                  ))}
                </div>
              )}
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

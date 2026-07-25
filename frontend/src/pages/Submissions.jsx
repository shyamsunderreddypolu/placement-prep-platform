import React, { useState, useEffect } from 'react';
import Navbar from '../components/Navbar';
import { getSubmissionHistory } from '../services/submissionService';
import { History, Calendar } from 'lucide-react';

const Submissions = () => {
  const [submissions, setSubmissions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const fetchHistory = async () => {
    setLoading(true);
    setError('');
    try {
      const data = await getSubmissionHistory();
      setSubmissions(data);
    } catch (err) {
      setError('Failed to load submission history. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchHistory();
  }, []);

  const getStatusClass = (status) => {
    return status === 'SOLVED' ? 'badge-easy' : 'badge-medium';
  };

  const formatDate = (dateString) => {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleDateString(undefined, {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  };

  return (
    <>
      <Navbar />
      <div className="page-container">
        <div className="page-header" style={{ marginBottom: '1.5rem' }}>
          <div>
            <h1 className="page-title">Practice Log History</h1>
            <p className="page-subtitle">Review your past coding submissions, solution notes, and completion statuses</p>
          </div>
        </div>

        {error && <div className="error-banner">{error}</div>}

        {loading ? (
          <div className="spinner-container">
            <div className="spinner"></div>
            <span>Loading practice logs...</span>
          </div>
        ) : submissions.length === 0 ? (
          <div style={{ textAlign: 'center', padding: '4rem 2rem', background: 'var(--bg-card)', borderRadius: '0.75rem' }}>
            <History size={48} style={{ color: 'var(--text-muted)', marginBottom: '1rem' }} />
            <h3>No submissions logged yet</h3>
            <p style={{ color: 'var(--text-muted)', marginTop: '0.5rem' }}>
              Head over to the Problems page to log your first attempt.
            </p>
          </div>
        ) : (
          <div className="submissions-list" style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
            {submissions.map((sub) => (
              <div
                key={sub.id}
                style={{
                  background: 'var(--bg-card)',
                  border: '1px solid var(--border-color)',
                  borderRadius: '0.75rem',
                  padding: '1.5rem',
                  backdropFilter: 'blur(12px)',
                }}
              >
                <div style={{ display: 'flex', flexWrap: 'wrap', alignItems: 'flex-start', justifyContent: 'space-between', gap: '1rem', marginBottom: '0.75rem' }}>
                  <div>
                    <h3 style={{ fontSize: '1.1rem', fontWeight: 600 }}>{sub.problemTitle}</h3>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginTop: '0.4rem', color: 'var(--text-muted)', fontSize: '0.85rem' }}>
                      <Calendar size={14} />
                      <span>{formatDate(sub.submittedAt)}</span>
                    </div>
                  </div>
                  <span className={`badge ${getStatusClass(sub.status)}`}>
                    {sub.status}
                  </span>
                </div>

                {sub.notes ? (
                  <div style={{ background: 'rgba(15, 23, 42, 0.4)', padding: '0.75rem 1rem', borderRadius: '0.5rem', borderLeft: '3px solid var(--primary-accent)', fontSize: '0.9rem', color: '#cbd5e1', whiteSpace: 'pre-wrap' }}>
                    {sub.notes}
                  </div>
                ) : (
                  <span style={{ fontSize: '0.85rem', color: 'var(--text-muted)', fontStyle: 'italic' }}>No notes provided.</span>
                )}
              </div>
            ))}
          </div>
        )}
      </div>
    </>
  );
};

export default Submissions;

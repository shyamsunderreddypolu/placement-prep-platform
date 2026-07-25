import React, { useState } from 'react';
import { logSubmission } from '../services/submissionService';
import { X, Send } from 'lucide-react';

const LogSubmissionModal = ({ problem, onClose, onSuccess }) => {
  const [status, setStatus] = useState('SOLVED');
  const [notes, setNotes] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      await logSubmission({
        problemId: problem.id,
        status,
        notes,
      });
      onSuccess();
      onClose();
    } catch (err) {
      setError('Failed to log submission. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay">
      <div className="modal-card">
        <div className="modal-header">
          <h3>Log Submission</h3>
          <button className="close-btn" onClick={onClose}><X size={18} /></button>
        </div>

        <div className="modal-body">
          <div style={{ marginBottom: '1.25rem' }}>
            <span style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>Problem</span>
            <div style={{ fontSize: '1.1rem', fontWeight: 600, marginTop: '0.25rem' }}>{problem.title}</div>
          </div>

          {error && <div className="error-banner">{error}</div>}

          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label htmlFor="status">Submission Status</label>
              <select
                id="status"
                className="form-input"
                value={status}
                onChange={(e) => setStatus(e.target.value)}
                required
              >
                <option value="SOLVED">SOLVED</option>
                <option value="ATTEMPTED">ATTEMPTED</option>
              </select>
            </div>

            <div className="form-group">
              <label htmlFor="notes">Personal Notes / Approach</label>
              <textarea
                id="notes"
                className="form-input"
                style={{ minHeight: '100px', resize: 'vertical' }}
                placeholder="Describe your solution approach, time complexity (e.g. O(N)), space complexity, or any blocks encountered..."
                value={notes}
                onChange={(e) => setNotes(e.target.value)}
              />
            </div>

            <button type="submit" className="btn-primary" disabled={loading}>
              {loading ? 'Submitting...' : <><Send size={16} /> Submit Attempt</>}
            </button>
          </form>
        </div>
      </div>
    </div>
  );
};

export default LogSubmissionModal;

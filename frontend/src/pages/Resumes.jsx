import React, { useState, useEffect } from 'react';
import Navbar from '../components/Navbar';
import AtsEvaluatorModal from '../components/AtsEvaluatorModal';
import { uploadResume, getUserResumes } from '../services/resumeService';
import { Upload, FileText, Calendar, ExternalLink, CheckCircle, Sparkles } from 'lucide-react';

const Resumes = () => {
  const [resumes, setResumes] = useState([]);
  const [selectedFile, setSelectedFile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [uploading, setUploading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [selectedResumeForAts, setSelectedResumeForAts] = useState(null);

  const fetchResumes = async () => {
    setLoading(true);
    setError('');
    try {
      const data = await getUserResumes();
      setResumes(data);
    } catch (err) {
      setError('Failed to load resume history. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchResumes();
  }, []);

  const handleFileChange = (e) => {
    if (e.target.files && e.target.files[0]) {
      setSelectedFile(e.target.files[0]);
      setError('');
      setSuccess('');
    }
  };

  const handleUpload = async (e) => {
    e.preventDefault();
    if (!selectedFile) {
      setError('Please select a file to upload.');
      return;
    }

    setUploading(true);
    setError('');
    setSuccess('');

    try {
      const uploaded = await uploadResume(selectedFile);
      setSuccess(`Successfully uploaded ${uploaded.fileName}!`);
      setSelectedFile(null);
      fetchResumes();
    } catch (err) {
      setError('Failed to upload resume. Please verify file format (PDF, DOCX).');
    } finally {
      setUploading(false);
    }
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

  const getFullFileUrl = (url) => {
    if (!url) return '#';
    if (url.startsWith('http://') || url.startsWith('https://')) {
      return url;
    }
    return `http://localhost:8080${url}`;
  };

  return (
    <>
      <Navbar />
      <div className="page-container">
        <div className="page-header" style={{ marginBottom: '1.5rem' }}>
          <div>
            <h1 className="page-title">Resume & ATS Management</h1>
            <p className="page-subtitle">Upload and store your placement resumes for ATS evaluation</p>
          </div>
        </div>

        {error && <div className="error-banner">{error}</div>}
        {success && (
          <div style={{ background: 'rgba(16, 185, 129, 0.15)', border: '1px solid rgba(16, 185, 129, 0.3)', color: '#34d399', padding: '0.75rem', borderRadius: '0.5rem', marginBottom: '1.25rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <CheckCircle size={18} />
            <span>{success}</span>
          </div>
        )}

        {/* Upload Form Card */}
        <div style={{ background: 'var(--bg-card)', border: '1px solid var(--border-color)', borderRadius: '1rem', padding: '2rem', marginBottom: '2rem', backdropFilter: 'blur(12px)' }}>
          <h3 style={{ fontSize: '1.2rem', fontWeight: 600, marginBottom: '1rem' }}>Upload New Resume</h3>
          <form onSubmit={handleUpload}>
            <div style={{ border: '2px dashed var(--border-color)', borderRadius: '0.75rem', padding: '2rem', textAlign: 'center', background: 'rgba(15, 23, 42, 0.3)', marginBottom: '1.25rem', position: 'relative' }}>
              <Upload size={36} style={{ color: 'var(--primary-accent)', marginBottom: '0.75rem' }} />
              <div style={{ fontSize: '0.95rem', fontWeight: 500 }}>
                {selectedFile ? selectedFile.name : 'Click or drop your resume file here'}
              </div>
              <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)', marginTop: '0.25rem' }}>
                Supports PDF, DOC, DOCX files (Max size: 10MB)
              </div>
              <input
                type="file"
                accept=".pdf,.doc,.docx"
                style={{ opacity: 0, position: 'absolute', inset: 0, cursor: 'pointer', width: '100%', height: '100%' }}
                onChange={handleFileChange}
              />
            </div>

            <button
              type="submit"
              className="btn-primary"
              style={{ width: 'auto', padding: '0.6rem 1.5rem' }}
              disabled={uploading || !selectedFile}
            >
              {uploading ? 'Uploading...' : 'Upload Resume'}
            </button>
          </form>
        </div>

        {/* Resumes List Section */}
        <h3 style={{ fontSize: '1.2rem', fontWeight: 600, marginBottom: '1rem' }}>Uploaded Resumes</h3>

        {loading ? (
          <div className="spinner-container">
            <div className="spinner"></div>
            <span>Loading resume library...</span>
          </div>
        ) : resumes.length === 0 ? (
          <div style={{ textAlign: 'center', padding: '4rem 2rem', background: 'var(--bg-card)', borderRadius: '0.75rem' }}>
            <FileText size={48} style={{ color: 'var(--text-muted)', marginBottom: '1rem' }} />
            <h3>No resumes uploaded yet</h3>
            <p style={{ color: 'var(--text-muted)', marginTop: '0.5rem' }}>
              Upload your first resume above to prepare for placement ATS screening.
            </p>
          </div>
        ) : (
          <div className="resumes-grid" style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(300px, 1fr))', gap: '1.5rem' }}>
            {resumes.map((resume) => (
              <div
                key={resume.id}
                style={{
                  background: 'var(--bg-card)',
                  border: '1px solid var(--border-color)',
                  borderRadius: '0.75rem',
                  padding: '1.5rem',
                  backdropFilter: 'blur(12px)',
                  display: 'flex',
                  flexDirection: 'column',
                  justifyContent: 'space-between',
                  gap: '1rem'
                }}
              >
                <div style={{ display: 'flex', alignItems: 'flex-start', gap: '1rem' }}>
                  <div style={{ background: 'rgba(99, 102, 241, 0.15)', padding: '0.75rem', borderRadius: '0.5rem', color: 'var(--primary-accent)' }}>
                    <FileText size={24} />
                  </div>
                  <div>
                    <h4 style={{ fontSize: '1rem', fontWeight: 600, wordBreak: 'break-word' }}>{resume.fileName}</h4>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '0.4rem', marginTop: '0.3rem', color: 'var(--text-muted)', fontSize: '0.8rem' }}>
                      <Calendar size={12} />
                      <span>{formatDate(resume.uploadedAt)}</span>
                    </div>
                  </div>
                </div>

                <div style={{ borderTop: '1px solid var(--border-color)', paddingTop: '0.75rem', display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: '0.5rem' }}>
                  <a
                    href={getFullFileUrl(resume.fileUrl)}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="link-btn"
                    style={{ fontSize: '0.85rem' }}
                  >
                    View Document <ExternalLink size={14} />
                  </a>
                  <button
                    className="btn-primary"
                    style={{ width: 'auto', padding: '0.4rem 0.75rem', fontSize: '0.8rem' }}
                    onClick={() => setSelectedResumeForAts(resume)}
                  >
                    <Sparkles size={14} /> ATS Score
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      {selectedResumeForAts && (
        <AtsEvaluatorModal
          resume={selectedResumeForAts}
          onClose={() => setSelectedResumeForAts(null)}
        />
      )}
    </>
  );
};

export default Resumes;
